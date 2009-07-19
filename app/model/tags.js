(function(ds) {
	function get() {
		var tags = new Array()
		ds.query("SELECT DISTINCT name FROM tags ORDER BY name ASC", function(rs) {
			while(rs.next()) tags.push(rs.getString("name"))
		})
		
		return tags
	}
	
	return {
		get: get
	}
})