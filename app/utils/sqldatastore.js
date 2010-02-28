(function(jdbcDriver, connectionString) {
	require("utils/common.js")
	
	function connect() {
		// TODO: database connection pooling here.. maybe
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
			log.severe(e)
			log.severe("rolling back")
			conn.rollback()
			
			throw e
		} finally {
			conn.close()
		}
	}
	
	return {
		query: function(sql, fn) {
			var conn = connect()
			query.curry(conn)(sql, fn)
			conn.close() 
		},
		transaction: transaction
	}
})
