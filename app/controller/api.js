(function() {
	function secure(fn) {
		if(request.authorization == "esh:" + config.sitepass) return fn()
		else return ["unauthorized"]
	}
	
	function create() {
		return secure(function() {
			var o = require("utils/json.js")(request.content)
			log.info(o.hello)

			return ["ok", "ok"]
		})
	}
	
	return {
		create: create
	}
})

