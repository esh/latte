CREATE TABLE posts(id integer primary key asc autoincrement, title, orig, timestamp);
CREATE TABLE tags(name, post REFERENCES posts (id));
