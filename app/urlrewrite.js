(function(url) {
	if(url.match(/^\/blog/) || url.match(/^\/admin/) || url.match(/^\/api/)) return url
	else if(url.match(/^\/[a-zA-Z]+\/?$/) || url.match(/^\/[a-zA-Z]+\/[0-9]+\/?$/)) return "/blog/show" + url
	else return url
})