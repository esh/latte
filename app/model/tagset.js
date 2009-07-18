(function(ds) {
	function get(tag) {
		var rs = ds.query("SELECT post FROM tags WHERE name='" + escape(tag) + "'")
		var posts = new Array()
		while(rs.next()) posts.push(rs.getInt("post"))
		return posts
	}
	
	return {
		get: get
	}
})