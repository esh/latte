package org.latte;

import org.latte.scripting.hostobjects.RequestProxy;

import java.io.IOException;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.latte.scripting.PrimitiveWrapFactory;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;

public class LatteServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(LatteServlet.class.getName());
	final private Scriptable parent;
	private Callable fn;

	private class Session extends ScriptableObject implements Serializable {
		@Override
		public String getClassName() {
			return "Session";
		}
	}

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

			Session session;
			if((session = (Session)request.getSession().getAttribute("latte.session")) == null) {
				session = new Session();
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
