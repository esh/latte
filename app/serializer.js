(function(key, title, path, tags) {
	require("utils/common.js")
	require("utils/imageutils.js")
	
	var model;

    if(key == null) {
    	model = new Object();
    	
		// get a key
		var all = ds.get("all")
		
		if(all == null) key = 0;
		else {
			all.keys.sort(function(a,b) { return a - b })
			key = all.keys[all.keys.length - 1] + 1
		}
		
		model.tags = new Array();
		
		shell("mkdir public/blog/" + key)
		log.debug("create new model: " + key)
	} else {
		// fetch old model
		model = ds.get(key)
		log.debug("edit model: " + key)
	}
	
	tags = tags != null ? tags.trim().toLowerCase().split(" ") : new Array()
	// add default tag
	if(tags.indexOf("all") == -1) tags.push("all")
	
	var cloud = ds.get("_cloud")
	if(cloud == null) {
		cloud = new Object()
		cloud.keys = new Object()
	}
			
	// get rid of tags that aren't in the new set
	model.tags.subtract(tags).forEach(function(o) {
		var mapping = ds.get(o)
		mapping.keys = mapping.keys.subtract([key])
		ds.put(o, mapping)
		
		cloud.keys[o]--
		if(cloud.keys[o] == 0) delete cloud.keys[o]
	})

	// add tags that aren't in the old set
	tags.subtract(model.tags).forEach(function(n) {
		var mapping = ds.get(n)
		if(mapping == null) {
			mapping = new Object()
			mapping.keys = new Array()
		}
		
		mapping.keys.push(key)
		mapping.keys.sort(function(a,b) { return a - b; })
		ds.put(n, mapping)
		
		if(cloud.keys[n] == undefined) cloud.keys[n] = 1
		else cloud.keys[n]++
	});

	// save the cloud
	ds.put("_cloud", cloud)
	
	// if we got a picture uploaded...
	if(path != undefined && path != null) {
		log.debug("using picture: " + path)
		var ext = path.substring(path.lastIndexOf('.')).toLowerCase();
		var newPath = "public/blog/" + key;
		
		// delete any old pics in the dir
		log.debug(shell("rm " + newPath + "/*"))
		
		// move the file over
		shell("mv " + path + " " + newPath + "/o" + ext)
		model.original = "/blog/" + key + "/o" + ext;
		
		// create preview
		log.debug(resize(newPath + "/o" + ext, newPath + "/p" + ".jpg", 480))
		// create thumb
		log.debug(generateThumb(newPath + "/o" + ext, newPath + "/t" + ".jpg"))
	}
		
	// save the model
	model.key = key;
	model.tags = tags;
	model.tags.sort(function(a,b) { return a - b; })
	model.title = title;
	model.date = new Date().toDateString()
	
	// handle the model
	ds.put(key, model)
		
	return model
})