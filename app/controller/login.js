(function() {
	function show() {
		return authenticate()
	}
	
	function authenticate() {
		if(params["passcode"] == config.get("site-pass")) {
			session["authorized"] = true
			return ["redirect", "/blog/show"]
		} else return ["ok", render("view/login/password.jhtml")]
	}
	
	return {
		show: show,
		authenticate: authenticate
	}
})