(function(url) {
	if(url.match(/^\/\w+\/[0-9]+$/) != null) return "/blog/show" + url
	else return url
})