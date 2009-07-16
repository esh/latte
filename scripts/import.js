var d = new java.io.File("data")

d.listFiles().forEach(function(f) {
	if(f.getPath().match(/\d+$/)) {
		var o = eval(readFile(f.getPath()))
		print("INSERT INTO posts VALUES(" + o.key  + ",\"" + o.title + "\",\"" + o.original.substring(o.original.indexOf(".") + 1) + "\",\"" + o.date + "\");")
		o.tags.forEach(function(t) {
			print("INSERT INTO tags VALUES(\"" + t + "\"," + o.key + ");")
		})
	}
})
