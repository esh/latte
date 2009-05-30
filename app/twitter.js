(function(model) {
	require("util/common.js")

	var auth = config.twitteruser + ":" + config.twitterpass)
	auth = "Basic " + auth.toBase64()
	
	var h = hopen("http://twitter.com/statuses/update.json", {"Authorization": auth})
	
	var status = "http://www.edomame.com/all/" + model.key + " - " + model.title
	if(status.length > 140) status.substring(0, 137) + "..."
	
	h.write("status=" + status.escapeURL())

	log.debug(h.read())
})
	
