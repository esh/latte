var args = String(request.getRequestURI()).substring(1).split("/")

// get the controller
var controller = args.shift();
if(controller == "") controller = require("/controller/root.js")()
else controller = require("/controller/" + controller + ".js")()

var action = args.shift();
if(action == undefined || action == "") action = "show"

// extract the params
var params = new Object()
if(request.getParameterMap().size() > 0) {
	request.getParameterMap().entrySet().toArray().forEach(function(entry) {
		if(request.getContentType().startsWith("multipart/form-data")) {
			if(entry.value instanceof java.lang.String) {
				var tmp = request.getAttribute(String(entry.getKey()))	
				var file = new java.io.File(tmp.getAbsoluteFile() + entry.value.substring(entry.value.indexOf(".")))
				tmp.renameTo(file)
				params[String(entry.getKey())] = file
			}
			else {
				params[String(entry.getKey())] = String(new java.lang.String(entry.value))
			}
		}
		else if(entry.value.getClass().isArray() && entry.value.getClass().getComponentType() == java.lang.String && entry.value.length == 1) {
			params[String(entry.getKey())] = String(entry.value[0])
		}
	})
}

// grab the session
var session
if((session = request.getSession().getAttribute("latte.session")) == null) {
	session = new Object()
	request.getSession().setAttribute("latte.session", session)
}
// run the controller and process the results
var result = controller[action].apply(controller, args)
switch(result[0]) {
case "ok":
	response.setContentType("text/html")
	response.setStatus(200)
	response.getWriter().append(result[1])
	log.info("handled " + request.getRequestURI())
	break
case "error":
	response.sendError(500)
	log.error(result[1])
	break
case "unauthorized":
	response.sendError(401)
	log.error("blocked " + request.getRequestURL())
	break
case "redirect":
	response.sendRedirect(result[1])
	log.info("handled " + request.getRequestURL())
	break
}