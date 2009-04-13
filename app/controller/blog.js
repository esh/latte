(function() {
	function secure(fn) {
		if(session["authorized"]) return fn()
		else return ["unauthorized"]
	}
	
	function create() {
		return secure(function() {
			return ["ok", render("view/blog/form.jhtml")]
		})
	}
	
	function show(type, key) {
		params["type"] = type == undefined | type == "" ? "all" : type
		var t = ds.get(params["type"]);
		if(t != null) {
			params["thumbs"] = t.keys
			params["thumbs"].sort(function(a, b) { return b - a })
		}
		
		if(params["thumbs"] == null || params["thumbs"].length == 0) {
			return ["error", type + " not defined"]
		}

		// get the specified key or get the last key in the cloud
		params["model"] = key != undefined && key != "" ? ds.get(key) : ds.get(params["thumbs"][0]);
		if(params["model"] == null) {
			return ["error", key + " not found"]	
		}
	
		params["tags"] = new Object();
		params["model"].tags.forEach(function(tag) {
			params["tags"][tag] = tag;
		});
		
		params["cloud"] = new Array();
		for(var tag in ds.get("_cloud").keys) {
			params["cloud"].push(tag);
		}
		params["cloud"].sort();
			
		return ["ok", render("view/blog/show.jhtml")]
	}
	
	function edit(pkey) {
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
		return secure(function() {
			params["models"] = new Array()
			ds.get("all").keys.forEach(function(key) {
				params["models"].push(ds.get(key))
			})
			
			return ["ok", render("view/blog/list.jhtml") ]
		})
	}
	
	function save() {
		return secure(function() {
			params["model"] = require("serializer.js")(params["key"], params["title"], params["upload"], params["tags"])
			return ["redirect", "/blog/show/all/" + params["model"].key]
		})
	}
	
	function remove(key) {
		return secure(function() {
			var model = ds.get(key);
			var cloud = ds.get("_cloud");
			
			// remove it from the tag cloud
			model.tags.forEach(function(tag) {
				var mapping = ds.get(tag);
				mapping.keys = mapping.keys.subtract([key]);
				ds.put(tag, mapping);
				
				cloud.keys[tag]--;
				if(cloud.keys[tag] == 0) {
					ds.remove(tag);
					delete cloud.keys[tag];
				}
			});
			
			ds.put("_cloud", cloud);
			ds.remove(key);	
			
			// remove images
			java.lang.Runtime.getRuntime().exec("rm -rf public/blog/" + key);	
			
			log.info("removed model " + key);
		
			return list()
		})
	}
	
	return {
		show: show,
		list: list,
		edit: edit,
		remove: remove,
		create: create,
		save: save
	}
})