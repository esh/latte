function getWidth(path) {
	return shell("identify -format %w " + path) * 1
}

function getHeight(path) {
	return shell("identify -format %h " + path) * 1
}

function resize(source, target, width) {
	return shell("convert -geometry " + width + "x " + source + " " + target)
}

function generateThumb(source, target) {
	var res = ""
    if(getWidth(source) < getHeight(source)) {
		res += shell("convert -geometry 80x " + source + " " + target)
		res += "\n"
		res += shell("convert -crop 80x80+0+" + (getHeight(target) - 80)/2 + " " + target + " " + target)
	} else {
		res += shell("convert -geometry x80 " + source + " " + target)
		res += "\n"
		res += shell("convert -crop 80x80+" + (getWidth(target) - 80)/2 + "+0 " + target + " " + target)
    }
    
    return res
}