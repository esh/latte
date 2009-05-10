(function(url) {
	if(url.match(/^\/.+\/[0-9]+$/) != null) return "/blog/show" + url
	else return url
})