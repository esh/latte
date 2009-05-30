register("config", config)
register("ds", require("utils/filedatastore.js")(config.dbcachesize))

require("system/mailbox.js")(
	config.mailhost, 
	config.mailuser,
	config.mailpass,
	function(subject, body, attachment) {
		require("twitter.js")(require("serializer.js")(null, subject, attachment, body))
	})