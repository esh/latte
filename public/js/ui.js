function nav(s) {
	window.location = "/blog/show/" + s.options[s.selectedIndex].value
}

function fadeIn(s) {
	$(s).fadeTo("slow", 1.0)
}

function loadMore(target, offset) {
	target.remove();
	
	var total = Math.min(offset + 24, thumbs.length) 
	var html = ""
	for(var i = offset ; i < total ; i++) {
		html += "<a href=\"/blog/show/" + type + "/" + thumbs[i] + "\">"
		html += "<img class=\"hidden\" src=\"/blog/" + thumbs[i] + "/t.jpg\" onload=\"fadeIn(this)\"/>"
		html += "</a>"
	}
	
	$("#stream").append(html)
	
	if(thumbs.length > total) {
		$("#stream").append("<div id=\"more\">load more</div>")
		$("#more").click(function() {
			loadMore($(this), total)
		})
	}
}

function renderThumbs(type, thumbs) {
	var html = ""
	for(var i = 0 ; i < Math.min(29, thumbs.length) ; i++) {
		html += "<a href=\"/blog/show/" + type + "/" + thumbs[i] + "\">"
		html += "<img src=\"/blog/" + thumbs[i] + "/t.jpg\"/>"
		html += "</a>"
	}
	$("#stream").append(html)

	if(thumbs.length > 29) {
		$("#stream").append("<div id=\"more\">load more</div>")	
		$("#more").click(function() {
			loadMore($(this), 29)
		})
	}
}