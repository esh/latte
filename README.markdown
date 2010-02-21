#Introduction
This project started life when I decided to learn javascript after reading <a href="http://steve-yegge.blogspot.com/2007/06/rhino-on-rails.html">Steve Yegge's post on Rhino on Rails</a>. Rather then wait for Steve to release RoR (he still hasn't) I decided to write a _very_ simplified version myself.

This is another MVC framework. It uses javascript on the server side and its powered by rhino. You can do stuff quickly because latte has a fast REPL cycle and because convention is favored over configuration.

I am currently using latte to power <http://www.edomame.com>, my personal website. 

#Features
* JavaScript awesomeness in the backend
* convention over configuration
* rapid REPL, just save and refresh your browser
* simple HTML templating
* java interop comes free
* no crazy magic, it's simple

#Dependencies
* JDK 1.5+
* apache ant

#Building the framework
1. clone the repository from github!
1. shell into the root directory
1. shell>ant

#A simple website
1. latte/> ./latte.sh app init.js
2. open http://localhost:8080/hello/world in a browser 

latte/app/controller/> cat hello.js
(function() {
	return {
		world: function() {
			return ["ok", "hello world"]
		}
	}
})

#A more complicated website
Check out the code at <http://www.github.com/esh/edomame> to see a full webapp built on latte. It has url rewriting, database access, a REST api, automatic redeployment via github pushes, and many more goodies.
