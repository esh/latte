(function(jdbcDriver, connectionString) {
	require("utils/common.js")
	
	function connect() {
		return jdbc(jdbcDriver, connectionString)
	}
	
	function update(conn, sql) {
		var stat = conn.createStatement()
		return stat.executeUpdate(sql)
	}
	
	function query(conn, sql) {
		var stat = conn.createStatement()
		return stat.executeQuery(sql)
	}
	
	function transaction(fn) {
		var conn = connect()
		//conn.setAutoCommit(false)
		update(conn, "BEGIN")
		try {
			fn({
				query: query.curry(conn),
				update: update.curry(conn)
			})
					
			update(conn, "COMMIT")
			//conn.setAutoCommit(true)
		}
		catch(e) {
			log.error(e)
			log.error("rolling back")
			//update(conn, "ROLLBACK")
		}
	}
	
	return {
		query: query.curry(connect()),
		update: update.curry(connect()),
		transaction: transaction
	}
})