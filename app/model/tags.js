(function(ds) {
	function get() {
		var rs = ds.query("SELECT DISTINCT name FROM tags ORDER BY name ASC")
		var tags = new Array()
		while(rs.next()) tags.push(rs.getString("name"))
		
		return tags
	}
	
	return {
		get: get
	}
})