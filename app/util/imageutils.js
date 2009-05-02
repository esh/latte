function getWidth(path) {
	var cmd = "identify -format %w " + path;
	var br = new java.io.BufferedReader(new java.io.InputStreamReader(java.lang.Runtime.getRuntime().exec(cmd).getInputStream()));

	return br.readLine() * 1;
}

function getHeight(path) {
	var cmd = "identify -format %h " + path;
	var br = new java.io.BufferedReader(new java.io.InputStreamReader(java.lang.Runtime.getRuntime().exec(cmd).getInputStream()));

	return br.readLine() * 1;
}

function resize(source, target, width) {
	var cmd = "convert -geometry " + width + "x " + source + " " + target;
	java.lang.Runtime.getRuntime().exec(cmd).waitFor();
}

function generateThumb(source, target) {
	var cmd;
	var w = getWidth(source);
    var h = getHeight(source);
    
    if(w < h) {
		cmd = "convert -geometry 80x " + source + " " + target;
		java.lang.Runtime.getRuntime().exec(cmd).waitFor();
      
		h = getHeight(target);
      
		cmd = "convert -crop 80x80+0+" + (h - 80)/2 + " " + target + " " + target;
		java.lang.Runtime.getRuntime().exec(cmd).waitFor();
	} else {
		cmd = "convert -geometry x80 " + source + " " + target;
     	java.lang.Runtime.getRuntime().exec(cmd).waitFor();
     	
 		w = getWidth(target);
		cmd = "convert -crop 80x80+" + (w - 80)/2 + "+0 " + target + " " + target;
		java.lang.Runtime.getRuntime().exec(cmd).waitFor();
    }
}