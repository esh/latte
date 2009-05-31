// rewrite the URL
var rewriter = require("urlrewrite.js")
if(rewriter != null) request.url = rewriter(request.url)

var args = request.url.substring(1).split("/")

// get the controller
var controller = args.shift();
if(controller == "") controller = require("/controller/root.js")()
else controller = require("/controller/" + controller + ".js")()

var action = args.shift();
if(action == undefined || action == "") action = "show"

// run the controller and process the results
var result = controller[action].apply(controller, args)
switch(result[0]) {
case "ok":
	response.setContentType("text/html")
	response.setStatus(200)
	response.getWriter().append(result[1])
	break
case "error":
	response.sendError(500)
	log.error(result[1])
	break
case "unauthorized":
	response.sendError(401)
	log.error("blocked " + request.url)
	break
case "redirect":
	response.sendRedirect(result[1])
	break
}