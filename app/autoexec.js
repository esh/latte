register("config", config)

// register the models in the global scope
var sqlds = require("utils/sqldatastore.js")("org.sqlite.JDBC","jdbc:sqlite:db")
register("model", {
	tagset: require("model/tagset.js")(sqlds),
	tags: require("model/tags.js")(sqlds),
	post: require("model/post.js")(sqlds)
})