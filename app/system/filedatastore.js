(function(cacheSize) {
	var log = org.apache.log4j.Logger.getLogger("Datastore.js");
	var rwl = new java.util.concurrent.locks.ReentrantReadWriteLock();
	var read = rwl.readLock();
	var write = rwl.writeLock();
	var cache = org.latte.util.LRUCache(cacheSize);
	var DATA_ROOT = "data/";
	
	function get(key) {
		try {
			read.lock();
			// if cached, return
			var value = cache.get(key);
			if(value != null) return value;
			
			log.debug("cache miss: " + key);
			// read from the file system and cache it
			var str = String(org.latte.util.FileReader.read(DATA_ROOT + key));
			value = eval(str);
			cache.put(key, value);
			return value;
		}
		catch(e) {
			log.error(e);
			return null;
		} finally {
			read.unlock();
		}
	}

	function put(key, value) {
		try {
			write.lock();
			var latest = get(key);
			
			// if this is new or the revisions match
			if(latest == null || latest._rev == value._rev) {
				// make sure we set a revision
				if(value._rev == undefined) value._rev = 0;
				// increment the revision
				else value._rev++;
			
				// put it in the cache		
				cache.put(key, value);
				
				var fos = new java.io.FileOutputStream(DATA_ROOT + "/" + key);
				var out = new java.io.PrintStream(fos);
				out.print(value.toSource());
				out.close();
			} else {
				throw new java.util.ConcurrentModificationException(key + " has been concurrently modified");
			}	
		} finally {
			write.unlock();
		}
	}
	
	function remove(key) {
		try {
			write.lock();

			cache.remove(key);
			new java.io.File(DATA_ROOT + "/" + key)["delete"]();
		} finally {
			write.unlock();
		}
	}
	
	return {
		get: get,
		put: put,
		remove: remove
	}
})