(function() {
	function secure(fn) {
		if(request.authorization == "esh:" + config.sitepass) return fn()
		else return ["unauthorized"]
	}
	
	function create() {
		return secure(function() {
			with(require("utils/json.js")(request.content)) {
				if(title != undefined && photo != undefined) {
					var path = "/tmp/" + Math.floor(Math.random() * 100000) + "." + ext
					log.info("api create: " + title + " => " + path)
										
					open(path, "base64").write(photo)
					require("twitter.js")(require("serializer.js")(null, title, path, tags))
					
					return ["ok", "ok"]
				} else {
					return ["ok", "missing title or photo"]
				}
			}
		})
	}
	
	return {
		create: create
	}
})

