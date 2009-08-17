package org.latte.scripting.hostobjects;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
	private static final Logger LOG = Logger.getLogger(HTTPServer.class);
	
	// port, staticcachesize, function(request, response, session)
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(((Integer)params[0]).intValue());
		server.setConnectors(new Connector[] { connector });
		
		org.mortbay.jetty.servlet.Context context = new org.mortbay.jetty.servlet.Context(server, "/", org.mortbay.jetty.servlet.Context.SESSIONS);
		context.addFilter(new FilterHolder(new MultiPartFilter()), "/*", Handler.REQUEST);		
		context.addServlet(new ServletHolder(new LatteServlet(scope, (Callable)params[2])), "/*");
		
		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("org.mortbay.jetty.servlet.Default.resourceBase", "public");
		initParams.put("org.mortbay.jetty.servlet.Default.maxCachedFiles", ((Integer)params[1]).toString());
		context.setInitParams(initParams);
		
		try {
			server.start();
		} catch (Exception e) {
			throw new JavaScriptException(e, "httpserver", 0);
		}

		return null;			
	}

	class LatteServlet extends DefaultServlet {
		final private Scriptable parent;
		final private Callable fn;
		
		private LatteServlet(Scriptable parent, Callable fn) {
			this.parent = parent;
			this.fn = fn;
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 5876743891237403945L;

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {			
			try {
				Context cx = ContextFactory.getGlobal().enterContext();

				Scriptable session;
				if((session = (Scriptable)request.getSession().getAttribute("latte.session")) == null) {
					session = new ScriptableObject() {
						@Override
						public String getClassName() { return "Session"; }
					};
					request.getSession().setAttribute("latte.session", session);
				}
				
				Scriptable scope = cx.newObject(parent);
				scope.setParentScope(parent);
				cx.setWrapFactory(new PrimitiveWrapFactory());
				fn.call(cx, scope, scope, new Object[] {
						new RequestProxy(request),
						response,
						session
				});
			} catch(Exception e) {
				LOG.fatal("", e);
				response.sendError(500);
			} finally {
				Context.exit();
			}
		}
		
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			if(isStaticFile(request.getRequestURI())) super.doGet(request, response);
			else doPost(request, response);
		}
	}
	
	private static boolean isStaticFile(String requestURI) {
		boolean numeric = true;
		
		for(int i = requestURI.length() - 1 ; i > 0 ; i--) {
			char c = requestURI.charAt(i);
			if(c == '/') {
				return false;
			}
			else if(c == '.' && !numeric) {
				return true;
			}
			else if(numeric && !(c == 0 || c == 1 || c == 2 || c == 3 || c == 4 || c == 5 || c == 6 || c == 7 || c == 8 || c == 9)) {
				numeric = false;
			}
		}
		
		return false;
	}
}
