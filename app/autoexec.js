register("config", config)
register("ds", require("utils/filedatastore.js")(config.dbcachesize))