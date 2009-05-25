(function(cacheSize) {
	var lock = new RWLock()
	var cache = org.latte.util.LRUCache(cacheSize)
	var DATA_ROOT = "data/"
	
	function get(key) {
		try {
			lock.readLock()
			// if cached, return
			var value = cache.get(key)
			if(value != null) return value
			
			log.debug("cache miss: " + key)
			// read from the file system and cache it
			value = eval(String(open(DATA_ROOT + key).read()))
			cache.put(key, value)
			return value
		} catch(e) {
			return null
		} finally {
			lock.readUnlock()
		}
	}

	function put(key, value) {
		try {
			lock.writeLock()
			var latest = get(key)

			// if this is new or the revisions match
			if(latest == null || latest._rev == value._rev) {
				// make sure we set a revision
				if(value._rev == undefined) value._rev = 0
				// increment the revision
				else value._rev++
			
				// put it in the cache		
				cache.put(key, value)
				
				open(DATA_ROOT + key).write(value.toSource())
			} else {
				throw key + " has been concurrently modified"
			}	
		} finally {
			lock.writeUnlock()
		}
	}
	
	function remove(key) {
		try {
			lock.writeLock()

			cache.remove(key)
			open(DATA_ROOT + key).remove()
		} finally {
			lock.writeUnlock()
		}
	}
	
	return {
		get: get,
		put: put,
		remove: remove
	}
})