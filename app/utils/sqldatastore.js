(function(jdbcDriver, connectionString) {
	require("utils/common.js")
	
	function connect() {
		return jdbc(jdbcDriver, connectionString)
	}
	
	function update(conn, sql) {
		conn.createStatement().executeUpdate(sql)
	}
	
	function query(conn, sql, fn) {
		try {
			var rs = conn.createStatement().executeQuery(sql)
			fn(rs)
		} finally {
			if(rs != undefined && rs != null) rs.close()
			conn.close()
		}
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
		}
		catch(e) {
			log.error(e)
			log.error("rolling back")
			conn.rollback()
			
			throw e
		} finally {
			conn.close()
		}
	}
	
	return {
		query: function(sql, fn) { query.curry(connect())(sql, fn) },
		transaction: transaction
	}
})