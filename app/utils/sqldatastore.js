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
		conn.setAutoCommit(false)
		try {
			fn({
				query: query.curry(conn),
				update: update.curry(conn)
			})
					
			conn.commit()
			conn.setAutoCommit(true)
		}
		catch(e) {
			log.error(e)
			log.error("rolling back")
			conn.rollback()
		}
	}
	
	return {
		query: query.curry(connect()),
		update: update.curry(connect()),
		transaction: transaction
	}
})