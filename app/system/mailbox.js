(function(host, user, pass, handler) {
	var session = javax.mail.Session.getInstance(java.lang.System.getProperties(), null);
	var store = session.getStore("imap");

	// Connect
	store.connect(host, user, pass);
	var folder = store.getFolder("INBOX");
	if (folder == null || !folder.exists()) {
		log.fatal("Invalid Inbox");
		throw "invalid inbox";
	}
	folder.open(javax.mail.Folder.READ_WRITE);
	
	log.info("listening to " + user + "@" + host)
	// Add messageCountListener to listen for new messages
	folder.addMessageCountListener(javax.mail.event.MessageCountListener({
		messagesAdded: function(ev) {
			try {
				log.info("got mail")
				ev.getMessages().forEach(function(msg) {
					var body;
					var attachment;	
					
					var subject = String(msg.getSubject());				
					var content = msg.getContent();
					
					if(content instanceof javax.mail.Multipart) {
						for(var i = 0 ; i < content.getCount() ; i++) {
							var part = content.getBodyPart(i);
							
							if(part.getDisposition() == null &&
							   part.getContentType().substring(0, 10) == "text/plain" &&
							   part.getContent().trim() != "") {
								body = String(part.getContent().trim());
							}
							else if(part.getDisposition() == javax.mail.Part.ATTACHMENT ||
									part.getDisposition() == javax.mail.Part.INLINE) {
								var ext = part.getFileName().substring(part.getFileName().indexOf("."));
								attachment = java.io.File.createTempFile("mail_", ext);
								part.saveFile(attachment);
							}
						}
					} else {
						body = String(content);
					}
					
					log.info("calling handler")
					handler(subject, body, attachment.toAbsolutePath())
				});
			} catch(e) {
				log.error(e);
			}	
		},	    
		messagesRemoved: function(ev) {
		}})
	);

	java.lang.Thread(java.lang.Runnable({	
		run: function() {
			for(;;) {
				try {
					java.lang.Thread.sleep(60000);
					folder.getMessageCount();
					// until imap idle works...
					//folder.idle();
				} catch(e) {
					log.error(e);
				}
			}
		}})
	).start();
})