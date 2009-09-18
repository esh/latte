function include(path, prefix) {
	var id = path.substring(0, path.lastIndexOf(".")) +  "." + open((prefix != undefined ? prefix : "") + path).timestamp() + ".generated" + path.substring(path.lastIndexOf(".")) 
	if(!open((prefix != undefined ? prefix : "") + id).exists()) shell("cp " + (prefix != undefined ? prefix : "") + path + " " + (prefix != undefined ? prefix : "") + id)
	return id 
}
