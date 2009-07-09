(function() {
	function secure(fn) {
		if(session["authorized"]) return fn()
		else return ["unauthorized"]
	}
		
	function detail(key) {
		if(String(key).match(/\d+/)) return ["ok", ds.get(key).toSource()]
		else return ["error", "expecting integer"]
	}
	
	function show(type) {
		var params = new Object()
		
		return function() {
			params["type"] = (type == undefined | type == "") ? "all" : type
			var t = ds.get(params["type"]);
			if(t != null) { 
				params["keys"] = t.keys
			} else return ["error", type + " not defined"]
		
			params["cloud"] = new Array()
			for(var tag in ds.get("_cloud").keys) {
				params["cloud"].push(tag)
			}		
			
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
			var model = ds.get(pkey)
			
			params["key"] = model.key
			params["title"] = model.title
			params["description"] = model.description
			params["original"] = model.original
			params["tags"] = model.tags.join(" ")
			
			return ["ok", render("view/blog/form.jhtml")]
		})
	}
	
	function list() {
		var params = new Array()
	
		return secure(function() {	
			ds.get("all").keys.forEach(function(key) {
				params.push(ds.get(key))
			})
			
			return ["ok", render("view/blog/list.jhtml") ]
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
		list: list,
		edit: edit,
		remove: remove,
		create: create,
		save: save
	}
})