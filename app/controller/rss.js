(function() {
	return {
		show: function() {
			require("utils/common.js")
			return ["ok", "<?rss version=\"1.0\"?>\n" + model.tagset.get("all").slice(-16).map(function(key) {
				var post = model.post.get(key)
				return  <item>
						<title>{post.title}</title>
						<description>{post.title}</description>
						<link>http://www.edomame.com/all/{post.key}</link>
						<guid>{post.key}</guid>
			      		</item>
				}).reduce(
					<rss version="2.0">
						<channel>
							<title>Edomame - a photo blog of Ed&apos;s adventures</title>
							<description>various captioned pictures from Ed</description>
							<link>http://www.edomame.com</link>
						</channel>
					</rss>, 
					function(rss, post) {
						rss.channel.item += post
						return rss
					})]
		}
	}
})

