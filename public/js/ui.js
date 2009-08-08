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

function loadUI(target, keys, focus, admin) {
	var LOAD_AMOUNT = 8
	var end = keys.indexOf(anchor)
	var start = Math.max(0, end - LOAD_AMOUNT)

	target.html(genHTML(start, end))
	callAJAX(start, end)

	$(window).scroll(function() {
		if(($(document).width() - $(window).width()) - $(window).scrollLeft() < 150 && start > 0) {
			var t = Math.max(0, start - 1)
			start = Math.max(0, start - LOAD_AMOUNT)
			target.html(target.html() + genHTML(start, t))
			callAJAX(start, t)
		}
	})

	function genHTML(start, end) {
		var html = new Array()
		for(var i = end ; i >= start ; i--) {
			html.push("<td id=\"")
			html.push(keys[i])
			html.push("\">")
                        html.push(keys[i] == focus ? "<div class=\"focus\">" : "<div class=\"post\">")
              		html.push("<a href=\"/blog/")
                        html.push(keys[i])
                       	html.push("/o.jpg\">")
                        html.push("<img src=\"/blog/")
                        html.push(keys[i])
                        html.push("/p.jpg\"")
                        html.push("/>")
                        html.push("</a></div></td>")
		}
		return html.join("")
	}

	function callAJAX(start, end) {
		for(var i = start ; i <= end ; i++) {
			$.getJSON("/blog/detail/" + keys[i], function(data) {
				var html = new Array()
				html.push($("#" + data.key + " div").html())
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
					html.push("<br/>")
					html.push("<a href=\"/blog/edit/")
					html.push(data.key)
					html.push("\">edit</a>")
					html.push("&nbsp;")
					html.push("<a href=\"/blog/remove/")
					html.push(data.key)
					html.push("\">remove</a>")
				}
				
				$("#" + data.key + " div").html(html.join(""))
			})	
		}

	}
}
