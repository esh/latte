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
import org.latte.scripting.ScriptCache;
import org.latte.util.Tuple;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.servlet.MultiPartFilter;

public class Main  {
	private static final Logger LOG = Logger.getLogger(Main.class);

	private ScriptCache loader;
	
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
		loader = new ScriptCache();			
		((Javascript)loader.get("autoexec.js")).eval(new Tuple[] { new Tuple<String, Object>("config", config) });		
		
		// start the server
		Server server = new Server(Integer.parseInt(config.getProperty("port")));
		org.mortbay.jetty.servlet.Context context = new org.mortbay.jetty.servlet.Context(server, "/", org.mortbay.jetty.servlet.Context.SESSIONS);
		
		context.addFilter(new FilterHolder(new MultiPartFilter()), "/*", Handler.REQUEST);		
		context.addServlet(new ServletHolder(new LatteServlet()), "/*");
		
		Map<String, String> initParams = new HashMap<String, String>();
		
		String PUBLIC_ROOT = "public";
		initParams.put("org.mortbay.jetty.servlet.Default.resourceBase", PUBLIC_ROOT);
		initParams.put("org.mortbay.jetty.servlet.Default.maxCachedFiles", config.getProperty("static-cache-size"));
		context.setInitParams(initParams);
		
		server.start();
	}
	
	class LatteServlet extends DefaultServlet {
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
				((Javascript)loader.get("system/dispatcher.js")).eval(new Tuple[] { 
						new Tuple<String, Object>("request", request),
						new Tuple<String, Object>("response", response) });
			} catch(Exception e) {
				LOG.fatal("something went wrong", e);
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
