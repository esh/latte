function redirect() {
	session['authorized'] = false;
	controller.redirect("/blog/show");
}