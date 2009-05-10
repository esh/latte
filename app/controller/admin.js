(function() {
	function show() {
		return login()
	}
	
	function login() {
		if(params["passcode"] == config.get("site-pass")) {
			session["authorized"] = true
			return ["redirect", "/"]
		} else return ["ok", render("view/admin/password.jhtml")]
	}
	
	function logout() {
		session["authorized"] = false
		return ["redirect", "/"]
	}
	
	return {
		show: show,
		login: login,
		logout: logout
	}
})