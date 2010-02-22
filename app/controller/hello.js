(function() {
	return {
		world: function() {
			return ["ok", "hello world"]
		},
		world2: function() {
			return ["ok", render("view/hello/world2.jhtml", { text: "hello world2" })]
		}
	}
})

