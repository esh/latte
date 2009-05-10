(function(model) {
	require("util/common.js")

	var uc = java.net.URL("http://twitter.com/statuses/update.json").openConnection()
	var auth = config.getProperty("twitter-user") + ":" + config.getProperty("twitter-pass")
	uc.setRequestProperty("Authorization", "Basic " + auth.toBase64())
	
	uc.setDoOutput(true)
	
	var writer = new java.io.OutputStreamWriter(uc.getOutputStream())
	
	var status = "http://www.edomame.com/all/" + model.key + " - " + model.title
	if(status.length > 140) status.substring(0, 137) + "..."
	
	writer.write("status=" + status.escapeURL())
	writer.close()

	var br = new java.io.BufferedReader(new java.io.InputStreamReader(uc.getInputStream()))
  
	var res
	while ((res = br.readLine()) != null) {
		log.debug(res)
 	}
  	br.close() 
})
	
