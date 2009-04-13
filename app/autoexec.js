register("config", config)
register("ds", require("system/filedatastore.js")(config.getProperty("db-cache-size")))

require("system/mailbox.js")(
	config.getProperty("mail-host"), 
	config.getProperty("mail-user"),
	config.getProperty("mail-pass"),
	function(subject, body, attachment) {
		require("twitter.js")(require("serializer.js")(null, subject, attachment, body))
	})

	
require("twitter.js")(ds.get("6"))