(function(key, title, upload, tags) {
	require("util/common.js")
	require("util/imageutils.js")
	
	var model;

    if(key == null) {
    	log.info("creating new model");
    	model = new Object();
    	
		// get a key		
		var all = ds.get("all");
		if(all == null) key = 0;
		else {
			all.keys.sort(function(a,b) { return a - b })
			log.info("incrementing " + all.keys[all.keys.length - 1])
			key = all.keys[all.keys.length - 1] + 1;
		}
		
		model.tags = new Array();
		
		new java.io.File("public/blog/" + key).mkdir();
	} else {
		// fetch old model
		model = ds.get(key)
	}
	
	tags = tags != null ? tags.trim().toLowerCase().split(" ") : new Array();
	// add default tag
	if(tags.indexOf("all") == -1) tags.push("all");
	
	var cloud = ds.get("_cloud");
	if(cloud == null) {
		cloud = new Object();
		cloud.keys = new Object();
	}
			
	// get rid of tags that aren't in the new set
	model.tags.subtract(tags).forEach(function(o) {
		var mapping = ds.get(o);
		mapping.keys = mapping.keys.subtract([key]);
		ds.put(o, mapping);
		
		cloud.keys[o]--;
		if(cloud.keys[o] == 0) delete cloud.keys[o];
	});

	// add tags that aren't in the old set
	tags.subtract(model.tags).forEach(function(n) {
		var mapping = ds.get(n);
		if(mapping == null) {
			mapping = new Object();
			mapping.keys = new Array();
		}
		
		mapping.keys.push(key);
		mapping.keys.sort(function(a,b) { return a - b; });
		ds.put(n, mapping);
		
		if(cloud.keys[n] == undefined) cloud.keys[n] = 1;
		else cloud.keys[n]++;
	});

	// save the cloud
	ds.put("_cloud", cloud);
	
	// if we got a picture uploaded...
	if(upload != null && upload instanceof java.io.File) {
		// delete any old pics in the dir
		java.lang.Runtime.getRuntime().exec("rm " + path + "/*").waitFor();
	
		var path = upload.getAbsolutePath();
		var ext = path.substring(path.lastIndexOf('.')).toLowerCase();
		path = "public/blog/" + key;
		
		if(upload.renameTo(new java.io.File(path + "/o" + ext))) {
			model.original = "/blog/" + key + "/o" + ext;
			log.info("saved " + path + "/o" + ext);
			
			// create preview
			resize(path + "/o" + ext, path + "/p" + ".jpg", 480);
			// create thumb
			generateThumb(path + "/o" + ext, path + "/t" + ".jpg")
		} else throw exception("could not save: " + upload)
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