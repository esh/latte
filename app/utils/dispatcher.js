(function(request, response, session) {
	(function() {
		try {
			request.url = require("routes.js")(request.url)
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
				log.warning("blocked " + request.address + " from " + request.url)
				break
			case "redirect":
				response.sendRedirect(result[1])
				break
			default:
				throw "unknown response"
			}
		} catch(e) {
			
			response.sendError(500)
			log.warning(request.address + " caused:")
			log.warning(e)
		}
	})()
})
