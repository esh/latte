register("config", config)
register("ds", require("utils/filedatastore.js")(config.dbcachesize))

var sqlds = require("utils/sqldatastore.js")("org.sqlite.JDBC","jdbc:sqlite:db")
register("model", {
	tagset: require("model/tagset.js")(sqlds),
	tags: require("model/tags.js")(sqlds),
	post: require("model/post.js")(sqlds)
})

model.post.persist(null, "test title", "test.asdf.jpg", "a b c")