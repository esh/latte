(function() {
	importPackage(com.google.appengine.api.datastore)
	var ds = DatastoreServiceFactory.getDatastoreService()

	function put(kind, data) {
		var entity = new Entity(kind)
		entity.setProperty("data", data.toSource())
		return KeyFactory.keyToString(ds.put(entity))
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

	return {
		put: put,
		get: get,
		find: find
 	}
})
