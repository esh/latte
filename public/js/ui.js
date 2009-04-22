function nav(s) {
	window.location = "/blog/show/" + s.options[s.selectedIndex].value
}

function fadeIn(s) {
	$(s).fadeTo("slow", 1.0)
}