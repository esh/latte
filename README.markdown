#Introduction
This project started life when I decided to learn javascript after reading <a href="http://steve-yegge.blogspot.com/2007/06/rhino-on-rails.html">Steve Yegge's post on Rhino on Rails</a>. Rather then wait for Steve to release RoR (he still hasn't) I decided to write a _very_ simplified version myself.

Latte is a serverside JavaScript MVC framework powered by Rhino. I use latte to power <http://www.edomame.com>, my personal website. 

#Features
* JavaScript awesomeness in the backend
* convention over configuration
* rapid REPL, just save and refresh your browser
* simple HTML templating - write the dynamic bits in JavaScript
* java interop comes free
* no crazy magic, it's simple

#Dependencies
* JDK 1.5+
* apache ant

#Building the framework
1. clone the repository from github!
1. shell into the root directory
1. latte> ant

#Starting the server

#So simple

1. latte/app/controller> cat hello.js
		(function() {
			return {
				world: function() {
					return ["ok", "hello world"]
				},
				world2: function() {
					return ["ok", render("view/hello/world2.jhtml", { text: "hello world2" })]
				}
			}
		})

1. latte> ./latte.sh init.js
1. check out http://localhost:8080/hello/world in a browser 
1. and.. http://localhost:8080/hello/world2

#A more complicated website
Check out the code at <http://www.github.com/esh/edomame> to see a full webapp built on latte. It has url rewriting, database access, a REST api, automatic redeployment via github pushes, and many more goodies.
