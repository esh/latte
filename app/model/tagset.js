(function(ds) {
	function get(tag) {
		var posts = new Array()
		ds.query("SELECT post FROM tags WHERE name='" + escape(tag) + "'", function(rs) {
			while(rs.next()) posts.push(rs.getInt("post"))
		})
		
		return posts
	}
	
	return {
		get: get
	}
})