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
			var post = model.post.get(pkey)
			params["key"] = post.key
			params["title"] = post.title
			params["description"] = post.description
			params["tags"] = post.tags.join(" ")
			
			return ["ok", render("view/blog/form.jhtml")]
		})
	}
	
	function save() {
		return secure(function() {
			var twit = request.params["key"] == null
			var post = model.post.persist(request.params["key"], request.params["title"], request.params["upload"], request.params["tags"])
			if(twit) require("twitter.js")(post)
			
			return ["redirect", "/blog/show/all/" + post.key]
		})
	}
	
	function remove(key) {
		return secure(function() {
			model.post.remove(key)
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
