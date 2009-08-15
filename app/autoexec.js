register("config", config)
register("db", require("utils/sqldatastore.js")("org.sqlite.JDBC","jdbc:sqlite:db"))
