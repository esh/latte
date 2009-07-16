if(!Array.indexOf) {
	// IE, I hate you I hate you I hate you
	Array.prototype.indexOf = function(o) {
		for(var i=0 ; i < this.length ; i++) {
			if(this[i]==o)  return i;
		}
		return -1;
	}
}

function nav(s) {
	window.location = "/" + s.options[s.selectedIndex].value
}

function fadeIn(s) {
	$(s).fadeTo("slow", 1.0)
}

function toAnchor(a) {
	$.scrollTo($("#" + a), 0)
}

function loadUI(target, keys, anchor, admin) {
	var NUM_DISPLAYED = 17
	var start = keys.indexOf(anchor)
	var end = start	
	var i = 1
	
	for(;;) {
		if(i < NUM_DISPLAYED && start > 0) {
			start--
			i++
		}
		if(i < NUM_DISPLAYED && end < keys.length - 1) {
			end++
			i++
		}
		if(i >= NUM_DISPLAYED || (start == 0 && end == keys.length - 1)) break
	}

	load(start, end, false)
	
	function morePrev() {
		start = Math.max(start - NUM_DISPLAYED, 0)
		load(start, end, true)
	}
	
	function moreNext() {
		end = Math.min(end + NUM_DISPLAYED, keys.length - 1)
		load(start, end, true)
	}
	
	function load(start, end, fade) {
		var html = "<table><tr>"
		if(start > 0) html += "<td><div id=\"morePrev\">load older</div></td>"
		for(var i = start ; i <= end ; i++) {
			html += "<td id=\"" + keys[i] + "\"" + (keys[i] == anchor ? " class=\"anchor\"" : "") + "/>"
		}
		if(end < keys.length - 1) html += "<td><div id=\"moreNext\">load newer</div></td>"
		html += "</tr></table>"
		target.html(html)

		$("#moreNext").click(function() {
			moreNext()
		})
		$("#morePrev").click(function() {
			morePrev()
		})
		
		for(var i = start ; i <= end ; i++) {
			$.getJSON("/blog/detail/" + keys[i], function(data) {
				var html = "<a href=\"" + data.original + "\">"
				html += "<img src=\"/blog/" + data.key + "/p.jpg\" " 
				html += (fade ? "class=\"hidden\" onload=\"fadeIn(this)\"" : "onload=\"toAnchor(" + anchor + ")\"") + "/>"
				html += "</a>"
				html += "<h1>" + data.title + "</h1>"
				html += "<h2>" + data.date + "</h2>"
				html += "Tagged as&nbsp;"
				$.each(data.tags, function(i, tag) {
					html += "<a href=\"" + tag + "\">" + tag + "</a>&nbsp;"
				})

				if(admin) {
					html += "<br/>"
					html += "<a href=\"/blog/edit/" + data.key + "\">edit</a>"
					html += "&nbsp;"
					html += "<a href=\"/blog/remove/" + data.key + "\">remove</a>"
				}
				
				$("#" + data.key).html(html)
			})	
		}
	}
}
