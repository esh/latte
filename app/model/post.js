(function(ds) {
	require("utils/common.js")
	require("utils/imageutils.js")
		
	function get(id) {
		var model
		ds.query("SELECT id, title, orig, timestamp FROM posts WHERE id=" + escape(id), function(rs) {
			if(rs.next()) {
				model = {
					key: rs.getInt("id"),
					title: unescape(rs.getString("title")),
					original: "/blog/" + rs.getInt("id") + "/o." + rs.getString("orig"),
					date: rs.getString("timestamp"),
					tags: function(post) {
						var tags = new Array()
						ds.query("SELECT name FROM tags WHERE post=" + escape(id), function(rs) {
							while(rs.next()) tags.push(rs.getString("name"))
						})
						
						return tags
					}(id)
				}
			} else throw "no such post: " + id
		})
		
		return model
	}
	
	function persist(key, title, path, tags) {
		ds.transaction(function(ds) {
			// split the tags into an array and ensure we have the "all" tag
			tags = tags != null && tags.trim().length > 0 ? tags.trim().toLowerCase().split(" ") : new Array()
			if(tags.indexOf("all") == -1) tags.push("all")
		
			// find the ext
			var ext = path != undefined && path != null && path.trim().length > 0 ? path.substring(path.lastIndexOf('.')).toLowerCase() : null
			
		    if(key == null) {
				ds.update("INSERT INTO posts (title, orig, timestamp) VALUES('" + escape(title) + "','" + escape(ext) + "','" + new Date().toDateString() + "')")
				var rs = ds.query("SELECT last_insert_rowid() AS id")
				if(rs.next()) key = rs.getInt("id")
				tags.forEach(function(tag) {
					ds.update("INSERT INTO tags (name, post) VALUES('" + escape(tag) + "'," + key + ")")
				})
				
				try { shell("mkdir public/blog/" + key) } catch(e) {}
				
				log.debug("new model: " + key)
			} else {
				ds.update("UPDATE posts SET title='" + escape(title) + "',orig='" + ext + "',timestamp='" + new Date().toDateString() + "' WHERE id=" + key)
				ds.update("DELETE from tags WHERE post=" + key)
				tags.forEach(function(tag) {
					ds.update("INSERT INTO tags (name, post) VALUES('" + escape(tag) + "'," + key + ")")
				})
			}
			
			if(ext != null) {
				log.debug("using picture: " + path)
				var newPath = "public/blog/" + key;
				try { shell("rm " + newPath + "/*") } catch(e) {}
								
				// move the file over
				shell("mv " + path + " " + newPath + "/o" + ext)
				model.original = "/blog/" + key + "/o" + ext;
				
				// create preview
				resize(newPath + "/o" + ext, newPath + "/p" + ".jpg", 370)
				// create thumb
				generateThumb(newPath + "/o" + ext, newPath + "/t" + ".jpg")
			}
		})	

		return {
			key: key,
			tags: tags,
			title: title,
			date: new Date().toDateString()
		}
	}
	
	function remove(key) {
		ds.transaction(function(ds) {
			ds.update("DELETE from tags WHERE post=" + key)
			ds.update("DELETE from posts WHERE id=" + key)
			try {shell("rm -rf public/blog/" + key) } catch(e) {}
		})
	}
	
	return {
		get: get,
		persist: persist,
		remove: remove
	}
})