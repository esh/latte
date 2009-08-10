(function() {
	function show() {
		require("utils/common.js")
		const NUM_DISPLAY = 8

		var rss = <rss version="2.0">
				<channel>
					<title>Edomame - a photo blog of Ed&apos;s adventures</title>
					<description>various captioned pictures from Ed</description>
					<link>http://www.edomame.com</link>
				</channel>
			   </rss>

		var keys = model.tagset.get("all")
		keys.slice(Math.max(0, keys.length - NUM_DISPLAY)).reverse().forEach(function(key) {
			var post = model.post.get(key)
			
			rss.channel.item += <item>
					<title>{post.title}</title>
					<description>{post.title}</description>
					<link>http://www.edomame.com/all/{key}</link>
				    </item>
		})


		return ["ok", "<?xml version=\"1.0\"?>\n" + rss.toXMLString()]
	}
	
	return {
		show: show
	}
})

