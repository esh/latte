package org.latte.scripting.hostobjects;

import org.latte.LatteServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import java.util.logging.Logger;
import org.latte.scripting.PrimitiveWrapFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.servlet.MultiPartFilter;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class HTTPServer implements Callable {
	private static final Logger LOG = Logger.getLogger(HTTPServer.class.getName());
	
	// port, staticcachesize, function(request, response, session)
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		ScriptableObject config = (ScriptableObject)params[0];
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(((Integer)config.get("port", config)).intValue());
		server.setConnectors(new Connector[] { connector });
		
		org.mortbay.jetty.servlet.Context context = new org.mortbay.jetty.servlet.Context(server, "/", org.mortbay.jetty.servlet.Context.SESSIONS);
		context.addFilter(new FilterHolder(new MultiPartFilter()), "/*", Handler.REQUEST);

		
		context.addServlet(new ServletHolder(new DefaultServlet()), "/public/*");
		context.addServlet(new ServletHolder(new LatteServlet(scope, (Callable)params[1])), "/");

		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("org.mortbay.jetty.servlet.Default.resourceBase", ".");
		initParams.put("org.mortbay.jetty.servlet.Default.maxCachedFiles", ((Integer)config.get("staticcachesize", config)).toString());
		context.setInitParams(initParams);
		
		try {
			server.start();
		} catch (Exception e) {
			throw new JavaScriptException(e, "httpserver", 0);
		}

		return null;			
	}
}
