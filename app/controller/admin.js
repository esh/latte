(function() {
	function show() {
		return login()
	}
	
	function login() {
		if(request.params["passcode"] == config.sitepass) {
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