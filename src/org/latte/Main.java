package org.latte;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;
import org.latte.scripting.hostobjects.ConfigProxy;
import org.latte.scripting.hostobjects.RequestProxy;
import org.latte.util.Tuple;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.servlet.MultiPartFilter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Main  {
	private static final Logger LOG = Logger.getLogger(Main.class);

	private ScriptLoader loader;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	@SuppressWarnings("unchecked")
	public Main() throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		
		// load config
		Properties config = new Properties();
		config.load(new FileInputStream("latte.properties"));

		// load latte core components
		loader = new ScriptLoader();
		((Javascript)loader.get("autoexec.js")).eval(new Tuple[] { new Tuple<String, Object>("config", new ConfigProxy(config))  });		
		
		// start the server
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Integer.parseInt(config.getProperty("port")));
		server.setConnectors(new Connector[] { connector });
		
		Context context = new Context(server, "/", org.mortbay.jetty.servlet.Context.SESSIONS);
		
		context.addFilter(new FilterHolder(new MultiPartFilter()), "/*", Handler.REQUEST);		
		context.addServlet(new ServletHolder(new LatteServlet()), "/*");
		
		Map<String, String> initParams = new HashMap<String, String>();
		
		String PUBLIC_ROOT = "public";
		initParams.put("org.mortbay.jetty.servlet.Default.resourceBase", PUBLIC_ROOT);
		initParams.put("org.mortbay.jetty.servlet.Default.maxCachedFiles", config.getProperty("staticcachesize"));
		context.setInitParams(initParams);
		
		server.start();
	}
	
	class LatteServlet extends DefaultServlet {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5876743891237403945L;

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {			
			dispatch(request, response);
		}
		
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			if(isStaticFile(request.getRequestURI())) super.doGet(request, response);
			else dispatch(request, response);
		}
		
		@SuppressWarnings("unchecked")
		private void dispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			try {
				// grab the session
				Scriptable session;
				if((session = (Scriptable)request.getSession().getAttribute("latte.session")) == null) {
					session = new ScriptableObject() {
						@Override
						public String getClassName() {
							return "Session";
						}
						
					};
					
					request.getSession().setAttribute("latte.session", session);
				}
				
				((Javascript)loader.get("dispatcher.js")).eval(new Tuple[] { 
						new Tuple<String, Object>("request", new RequestProxy(request)),
						new Tuple<String, Object>("response", response),
						new Tuple<String, Object>("session", session),});
			} catch(Exception e) {
				LOG.fatal("", e);
				response.sendError(500);
			}
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
