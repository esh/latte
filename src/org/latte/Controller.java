package org.latte;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptCache;
import org.latte.util.Tuple;
import org.mozilla.javascript.NativeArray;

public class Controller {
	private static final Logger LOG = Logger.getLogger(Controller.class);
	private static final String LATTE_SESSION = "latte.session";
	
	private final ScriptCache loader;

	
	public Controller(ScriptCache loader) {
		this.loader = loader;
	}
	
	private Queue<String> splitRequests(String requestURI) {
		Queue<String> req = new LinkedList<String>();
		
		for(String param : requestURI.split("/")) {
			if(!param.equals("")) {
				req.add(param);
			}
		}
		
		return req;
	}
	
	private String getController(Queue<String> req) {
		if(req.size() == 0) {
			return null;
		} else {
			return req.remove();
		}
	}
	
	private Javascript getControllerScript(String controller) throws Exception {
		String path;
		if(controller == null) path = LatteConstants.CONTROLLER_ROOT + "root.js";
		else path = LatteConstants.CONTROLLER_ROOT + controller + ".js";
		
		return (Javascript)loader.get(path);
	}
	
	private String getAction(Queue<String> req) {
		if(req.size() == 0) {
			// default action
			return "redirect";
		} else {
			return req.remove();
		}
	}
	
	private Object[] getArgs(String action, Queue<String>req) {
		if(req.size() == 0) {
			return new Object[] {};
		} else {
			return req.remove().split(",");
		}
	}
	
	@SuppressWarnings("unchecked")
	private Tuple<String, Object>[] getBindings(Object params, Object session, PrintWriter writer) {		
		// this bit of ugliness is required as java does not support generic arrays
		return new Tuple[] { 
				new Tuple<String, Object>("writer", writer),
				new Tuple<String, Object>("params", params),
				new Tuple<String, Object>("session", session),
//				new Tuple<String, Object>("view", new View(loader, params, session, writer)),
			};
	}
	
	private void runController(HttpServletRequest request, HttpServletResponse response, Tuple<String, Object>[] env) throws Exception {
		try {
			Queue<String> req = splitRequests(request.getRequestURI());
			String controller = getController(req);
			Javascript script = getControllerScript(controller);
			String action = getAction(req);
			
			// run the javascript controller
	//		script.invoke(action, getArgs(action, req), env);
			
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			
			LOG.info("handled " + request.getRequestURI());
		}
		catch(Exception e) { /* no filter */
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			LOG.error(e);
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// retrieve or get the session
		Object session = (NativeArray)request.getSession().getAttribute(LATTE_SESSION);
		if(session == null) {
			session = new NativeArray(0);
			request.getSession().setAttribute(LATTE_SESSION, session);
		}
		
		// process the post data
		NativeArray params = new NativeArray(0);
		Map requestParams = request.getParameterMap();
		for(Object o : requestParams.entrySet()) {
			Map.Entry entry = (Map.Entry)o;
			Object value = entry.getValue();

			if(request.getContentType().startsWith("multipart/form-data")) {
				if(value instanceof byte[]) {
					byte[] b = (byte[])value;
					if(b.length > 0) params.put(entry.getKey().toString(), params, new String((byte[])value));
					else params.put(entry.getKey().toString(), params, null);
				}
				else if(value instanceof String) {
					File tmp = (File)request.getAttribute((String)entry.getKey());
					
					String path = (String)value;
					String ext = path.substring(path.indexOf("."));
					File file = new File(tmp.getAbsoluteFile() + ext);
					tmp.renameTo(file);
					params.put(entry.getKey().toString(), params, file);
				}
			}
			else if(value instanceof String[] && ((String[])value).length == 1) {
				params.put(entry.getKey().toString(), params, ((String[])value)[0]);
			}
			else {
				params.put(entry.getKey().toString(), params, null);
			}
		}
		
		runController(request, response, getBindings(params, session, response.getWriter()));
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// retrieve or get the session
		Object session = (NativeArray)request.getSession().getAttribute(LATTE_SESSION);
		if(session == null) {
			session = new NativeArray(0);
			request.getSession().setAttribute(LATTE_SESSION, session);
		}
				
		runController(request, response, getBindings(new NativeArray(0), session, response.getWriter()));
	}
}