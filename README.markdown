#Introduction
This project started life when I decided to learn javascript after reading <a href="http://steve-yegge.blogspot.com/2007/06/rhino-on-rails.html">Steve Yegge's post on Rhino on Rails</a>. Rather then wait for Steve to release RoR (he still hasn't) I decided to write a _very_ simplified version myself.

As you can guess, this is yet another MVC web framework. It uses javascript on the server side and its powered by rhino. JDBC, basic file io, HTTP communication are exposed via host objects.

I am currently using latte to power <http://www.edomame.com>, my personal website. Its a simple picture blog that I can update via email or it's REST interface. The code for edomame is bundled into this project.

#Dependencies
* \*nix OS
* imagemagick
* JDK 1.5+
* apache ant

#Installing latte
1. clone the respository from github!
1. in a shell, move into the root directory
1. shell>ant
1. shell>./latte run scripts/db.create.js
1. shell>mkdir public/blog
1. shell>./latte app init.js
1. open http://localhost:8080 from a browser!
