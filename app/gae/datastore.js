(function() {
	require("utils/common.js")

	importPackage(com.google.appengine.api.datastore)
	var ds = DatastoreServiceFactory.getDatastoreService()

	function put(kind, data) {
		var entity = new Entity(kind)
		entity.setProperty("data", data.toSource())
		return KeyFactory.keyToString(ds.put(entity))
	}

	function update(key, data) {
		var entity = new Entity(KeyFactory.stringToKey(key))
		entity.setProperty("data", data.toSource())
		ds.put(entity)
	}
	
	function get(key) {
		var entity = ds.get(KeyFactory.stringToKey(key))
		return eval(entity.getProperty("data"))
	}

	function find(kind) {
		var results = new Array()
		var itor = ds.prepare(new Query(kind)).asIterator()
		while(itor.hasNext()) {
			results.push(eval(itor.next().getProperty("data")))
		}

		return results
	}

	function remove(key) {
		ds["delete"](key)
	}

	function transaction(fn) {
		var t = ds.beginTransaction()
		try {
			fn({
				put: put,
				get: get,
				find: find,
				update: update,
				remove: remove
			})
			t.commit()
		}
		catch(e) {
			log.severe(e)
			log.severe("rolling back")
			t.rollback()

			throw e
		}
	}

	return {
		put: put,
		get: get,
		find: find,
		update: update,
		remove: remove,
		transaction: transaction
 	}
})
