var config = require("config.js")

httpserver(config, function(request, response, session) {
	// kind of evil, form a closure for access to request, response, session, config
	(function() {
		try {
			var rewriter = require("urlrewrite.js")
			if(rewriter != null) request.url = rewriter(request.url)
			
			var args = request.url.substring(1).split("/")
			
			// get the controller
			var controller = args.shift();
			if(controller == "") controller = require("controller/root.js")()
			else controller = require("controller/" + controller + ".js")()
				
			var action = args.shift();
			if(action == undefined || action == "") action = "show"
			
			var result = controller[action].apply(controller, args)
			switch(result[0]) {
			case "ok":
				if(result.length >= 3) response.setContentType(result[2])
				else response.setContentType("text/html; charset=UTF-8")
				response.setCharacterEncoding("UTF-8")
				response.setStatus(200)
				response.getWriter().append(result[1])
				break
			case "unauthorized":
				response.sendError(401)
				log.error("blocked " + request.address + " from " + request.url)
				break
			case "redirect":
				response.sendRedirect(result[1])
				break
			default:
				throw "unknown response"
			}
		} catch(e) {
			
			response.sendError(500)
			log.error(request.address + " caused:")
			log.error(e)
		}
	})()
})
