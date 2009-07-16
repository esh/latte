(function(jdbcDriver, connectionString) {
	function connect() {
		return jdbc(jdbcDriver, connectionString)
	}
	
	function query(sql) {
		var conn = connect()
		var stat = conn.createStatement()
		return stat.executeQuery(sql)
	}
	
	return {
		query: query
	}
})