(function() {
	var post = require("model/post.js")(db)
	var tagset = require("model/tagset.js")(db)
	var tags = require("model/tags.js")(db)

	function secure(fn) {
		if(session["authorized"]) return fn()
		else return ["unauthorized"]
	}
			
	function detail(key) {
		return ["ok", post.get(key).toSource()]
	}
	
	function show(type) {
		var params = new Object()
		return function() {
			params["type"] = (type == undefined | type == "") ? "all" : type
			params["keys"] = tagset.get(params["type"])
			params["cloud"] = tags.get()
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
			var p = post.get(pkey)
			params["key"] = p.key
			params["title"] = p.title
			params["description"] = p.description
			params["tags"] = p.tags.join(" ")
			
			return ["ok", render("view/blog/form.jhtml")]
		})
	}
	
	function save() {
		return secure(function() {
			var twit = request.params["key"] == null
			var p = post.persist(request.params["key"], request.params["title"], request.params["upload"], request.params["tags"])
			if(twit) require("twitter.js")(p)
			
			return ["redirect", "/blog/show/all/" + post.key]
		})
	}
	
	function remove(key) {
		return secure(function() {
			post.remove(key)
			return show()
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
