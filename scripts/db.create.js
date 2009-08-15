var conn = jdbc("org.sqlite.JDBC","jdbc:sqlite:db")
conn.createStatement().executeUpdate("CREATE TABLE posts(id integer primary key asc autoincrement, title, timestamp)")
conn.createStatement().executeUpdate("CREATE TABLE tags(name, post REFERENCES posts (id))")
