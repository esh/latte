(function() {
	function secure(fn) {
		if(session["authorized"]) return fn()
		else return ["unauthorized"]
	}
			
	function detail(key) {
		return ["ok", model.post.get(key).toSource()]
	}
	
	function show(type) {
		var params = new Object()
		return function() {
			params["type"] = (type == undefined | type == "") ? "all" : type
			params["keys"] = model.tagset.get(params["type"])
			params["cloud"] = model.tags.get()
			params["admin"] = session["authorized"] == true
			
			return ["ok", render("view/blog/show.jhtml")]
		}()
	}
	
	function create() {
		var params = new Object()
		return secure(function() {	
			return ["ok", render("view/blog/form.jhtml")]
		})
	}
	
	function edit(pkey) {
		var params = new Object()
		return secure(function() {
			var model = model.post.get(pkey)
			params["key"] = model.key
			params["title"] = model.title
			params["description"] = model.description
			params["original"] = model.original
			params["tags"] = model.tags.join(" ")
			
			return ["ok", render("view/blog/form.jhtml")]
		})
	}
	
	function save() {
		return secure(function() {
			var model = new Object()
			model = require("serializer.js")(request.params["key"], request.params["title"], request.params["upload"], request.params["tags"])
			return ["redirect", "/blog/show/all/" + model.key]
		})
	}
	
	function remove(key) {
		return secure(function() {
			var model = ds.get(key)
			var cloud = ds.get("_cloud")
			
			// remove it from the tag cloud
			model.tags.forEach(function(tag) {
				var mapping = ds.get(tag)
				mapping.keys = mapping.keys.subtract([key])
				ds.put(tag, mapping)
				
				cloud.keys[tag]--;
				if(cloud.keys[tag] == 0) {
					ds.remove(tag)
					delete cloud.keys[tag]
				}
			});
			
			ds.put("_cloud", cloud)
			ds.remove(key)
			
			// remove images
			shell("rm -rf public/blog/" + key)

			return list()
		})
	}
	
	return {
		show: show,
		detail: detail,
		edit: edit,
		remove: remove,
		create: create,
		save: save
	}
})
