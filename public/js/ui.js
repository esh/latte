function nav(s) {
	window.location = "/blog/show/" + s.options[s.selectedIndex].value
}

function fadeIn(s) {
	$(s).fadeTo("slow", 1.0)
}

function loadMore(target, type, thumbs, offset) {
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
			loadMore($(this), type, thumbs, total)
		})
	}
}

function renderThumbs(type, thumbs, current) {
	var html = ""
	var total = Math.min(thumbs.indexOf(current) + 29, thumbs.length)
	for(var i = 0 ; i < total ; i++) {
		html += "<a href=\"/blog/show/" + type + "/" + thumbs[i] + "\">"
		html += "<img src=\"/blog/" + thumbs[i] + "/t.jpg\"/>"
		html += "</a>"
	}
	$("#stream").append(html)

	if(thumbs.length > total) {
		$("#stream").append("<div id=\"more\">load more</div>")	
		$("#more").click(function() {
			loadMore($(this), type, thumbs, total)
		})
	}
}