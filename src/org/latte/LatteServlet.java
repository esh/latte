package org.latte;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;

import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStreamReader;
import java.io.IOException;

import org.latte.scripting.PrimitiveWrapFactory;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;

import org.mozilla.javascript.JSON;
import org.apache.commons.codec.binary.Base64;

public class LatteServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(LatteServlet.class.getName());
	final private Scriptable parent;
	private Callable fn;

	public LatteServlet() throws Exception {
		ScriptLoader loader = new ScriptLoader();
		this.parent = loader.getRoot();

		loader.register("httpserver", new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				fn = (Callable)params[1];	
				return null;
			}
		});

		((Javascript)loader.get("init.js")).eval(null);
	}
	
	public LatteServlet(Scriptable parent, Callable fn) {
		this.parent = parent;
		this.fn = fn;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5876743891237403945L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {			
		try {
			Context cx = ContextFactory.getGlobal().enterContext();
			Scriptable session;
			String sessionSource;
			if((sessionSource = (String)request.getSession().getAttribute("latte.session")) == null) {
				session = cx.newObject(parent);
			} else {
				session = (Scriptable)JSON.fromString(cx, parent, sessionSource);
			}

			cx.setWrapFactory(new PrimitiveWrapFactory());

			Scriptable requestProxy = cx.newObject(parent);
			Scriptable params = cx.newObject(parent);
			Enumeration paramNames = request.getParameterNames();
			while(paramNames.hasMoreElements()) {
				String key = (String)paramNames.nextElement();
				String[] values = request.getParameterValues(key);
				if(values.length == 1) {
					ScriptableObject.putProperty(params, key, values[0]);
				} else {
					ScriptableObject.putProperty(params, key, values);
				}
			}
			ScriptableObject.putProperty(requestProxy, "params", params);
			ScriptableObject.putProperty(requestProxy, "address", request.getRemoteAddr());
			ScriptableObject.putProperty(requestProxy, "remoteaddr", request.getRemoteAddr());
			ScriptableObject.putProperty(requestProxy, "url", request.getServletPath());
					
			String authorization = (String)request.getHeader("Authorization");
			if(authorization != null && authorization.startsWith("Basic"))  {
				ScriptableObject.putProperty(requestProxy, "authorization", new String(new Base64().decode(authorization.split(" ")[1].getBytes())));
			}
		
			if(request.getContentType() != null && request.getContentType().contains("application/json")) {
				StringBuffer sb = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
				String line;
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				ScriptableObject.putProperty(requestProxy, "content", sb.toString());
			}

			fn.call(cx, parent, parent, new Object[] {
					requestProxy,
					response,
					session
			});

			sessionSource = JSON.stringify(cx, parent, session);
			request.getSession().setAttribute("latte.session", sessionSource);
		} catch(Exception e) {
			LOG.log(Level.SEVERE, "", e);
			response.sendError(500);
		} finally {
			Context.exit();
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
