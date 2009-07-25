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
	var NUM_DISPLAYED = 9
	var end = Math.min(keys.length - 1, keys.indexOf(anchor) + NUM_DISPLAYED)
	var start = Math.max(0, end - NUM_DISPLAYED) 	
	
	load(start, end, false)
	
	function morePrev() {
		start = Math.max(start - NUM_DISPLAYED, 0)
		load(start, end, true)
	}
	
	function moreNext() {
		end = Math.min(end + NUM_DISPLAYED, keys.length - 1)
		load(start, end, true)
	}

	function renderGrid(thumbs) {
		var html =  ""
		for(var i = 0 ; i < keys.length ; i++) {
			html += "<img src=\"/blog/" + thumbs[i] + "/t.jpg\" class=\"thumb\"/>" 
		}

		return html
	}
	
	function load(start, end, fade) {
		var html = new Array()
		html.push("<table><tr>")
		if(start > 0) {
			html.push("<td><div id=\"morePrev\" class=\"preview\">")
			html.push(renderGrid(keys.slice(Math.max(0, start - NUM_DISPLAYED),start)))
			html.push("<h1>Load More</h1></div></td>")
		}
		for(var i = start ; i <= end ; i++) {
			html.push("<td id=\"")
			html.push(keys[i])
			html.push("\"")
			html.push((keys[i] == anchor ? " class=\"anchor\"" : ""))
			html.push("/>")
		}
		if(end < keys.length - 1) {
			html.push("<td><div id=\"moreNext\" class=\"preview\">")
			html.push(renderGrid(keys.slice(end, Math.min(keys.length, end + NUM_DISPLAYED))))
			html.push("<h1>Load More</h1></div></td>")
		}
		html.push("</tr></table>")
		target.html(html.join(""))

		$("#moreNext").click(function() {
			moreNext()
		})
		$("#morePrev").click(function() {
			morePrev()
		})
		
		for(var i = start ; i <= end ; i++) {
			$.getJSON("/blog/detail/" + keys[i], function(data) {
				var html = new Array()
				html.push("<a href=\"")
				html.push(data.original)
				html.push("\">")
				html.push("<img src=\"/blog/")
				html.push(data.key)
				html.push("/p.jpg\" ") 
				html.push(fade ? "class=\"hidden\" onload=\"fadeIn(this)\"" : ("onload=\"toAnchor(" + anchor + ")\""))
				html.push("/>")
				html.push("</a>")
				html.push("<h1>")
				html.push(data.title)
				html.push("</h1>")
				html.push("<h2>")
				html.push(data.date)
				html.push("</h2>")
				html.push("Tagged as&nbsp;")
				$.each(data.tags, function(i, tag) {
					html.push("<a href=\"")
					html.push(tag)
					html.push("\">")
					html.push(tag)
					html.push("</a>&nbsp;")
				})

				if(admin) {
					html.join("<br/>")
					html.join("<a href=\"/blog/edit/")
					html.join(data.key)
					html.join("\">edit</a>")
					html.join("&nbsp;")
					html.join("<a href=\"/blog/remove/")
					html.join(data.key)
					html.join("\">remove</a>")
				}
				
				$("#" + data.key).html(html.join(""))
			})	
		}
	}
}
