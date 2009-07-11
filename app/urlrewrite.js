(function(url) {
	if(url.match(/^\/blog/) || url.match(/^\/admin/) || url.match(/^\/api/)) return url
	
	var res = url.match(/^(\/[a-zA-Z]+)(#[0-9]+)?$/)
	if(res != null && res[1] != null) return "/blog/show" + res[1]
	else return url
})
