(function(ds) {
	require("utils/common.js")
	require("utils/imageutils.js")
		
	function get(id) {
		var rs = ds.query("SELECT id, title, orig, timestamp FROM posts WHERE id=" + escape(id))
		if(rs.next()) {
			return {
				key: rs.getInt("id"),
				title: unescape(rs.getString("title")),
				original: "/blog/" + rs.getInt("id") + "/o." + rs.getString("orig"),
				date: rs.getString("timestamp"),
				tags: function(post) {
					var rs = ds.query("SELECT name FROM tags WHERE post=" + escape(id))
					var tags = new Array()
					while(rs.next()) tags.push(rs.getString("name"))
					return tags
				}(id)
			}
		} else throw "no such post: " + id
	}
	
	function persist(key, title, path, tags) {
		var model;
	
		// split the tags into an array and ensure we have the "all" tag
		tags = tags != null && tags.trim().length > 0 ? tags.trim().toLowerCase().split(" ") : new Array()
		if(tags.indexOf("all") == -1) tags.push("all")
		
		ds.transaction(function(ds) {
		    if(key == null) {
		    	model = new Object();
		    	
		    	var ext = path.match(/\.(\w+)$/)[1]
				ds.update("INSERT INTO posts (title, orig, timestamp) VALUES('" + escape(title) + "','" + escape(path) + "','" + new Date().toDateString() + "')")
				var rs = ds.query("SELECT last_insert_rowid() AS id")
				if(rs.next()) key = rs.getInt("id")
				tags.forEach(function(tag) {
					ds.update("INSERT INTO tags (name, post) VALUES('" + escape(tag) + "'," + key + ")")
				})
				
				//shell("mkdir public/blog/" + key)
				log.debug("persisted new model: " + key)
			} else {
				
			}
		})
		

		/*
		tags = tags != null ? tags.trim().toLowerCase().split(" ") : new Array()
		
		// add default tag
		if(tags.indexOf("all") == -1) tags.push("all")
		
		var cloud = ds.get("_cloud")
		if(cloud == null) {
			cloud = new Object()
			cloud.keys = new Object()
		}
				
		// get rid of tags that aren't in the new set
		model.tags.subtract(tags).forEach(function(o) {
			var mapping = ds.get(o)
			mapping.keys = mapping.keys.subtract([key])
			ds.put(o, mapping)
			
			cloud.keys[o]--
			if(cloud.keys[o] == 0) delete cloud.keys[o]
		})
	
		// add tags that aren't in the old set
		tags.subtract(model.tags).forEach(function(n) {
			var mapping = ds.get(n)
			if(mapping == null) {
				mapping = new Object()
				mapping.keys = new Array()
			}
			
			mapping.keys.push(key)
			mapping.keys.sort(function(a,b) { return a - b; })
			ds.put(n, mapping)
			
			if(cloud.keys[n] == undefined) cloud.keys[n] = 1
			else cloud.keys[n]++
		})
		
		// save the cloud
		ds.put("_cloud", cloud)
		
		// if we got a picture uploaded...
		if(path != undefined && path != null) {
			log.debug("using picture: " + path)
			var ext = path.substring(path.lastIndexOf('.')).toLowerCase();
			var newPath = "public/blog/" + key;
			
			// delete any old pics in the dir
			log.debug(shell("rm " + newPath + "/*"))
			
			// move the file over
			shell("mv " + path + " " + newPath + "/o" + ext)
			model.original = "/blog/" + key + "/o" + ext;
			
			// create preview
			log.debug(resize(newPath + "/o" + ext, newPath + "/p" + ".jpg", 370))
			// create thumb
			log.debug(generateThumb(newPath + "/o" + ext, newPath + "/t" + ".jpg"))
		}
			
		// save the model
		model.key = key;
		model.tags = tags;
		model.tags.sort(function(a,b) { return a - b; })
		model.title = title;
		model.date = new Date().toDateString()
		
		// handle the model
		ds.put(key, model)
			
		return model
		*/
	}
	
	return {
		get: get,
		persist: persist
	}
})