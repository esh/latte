package org.latte.scripting;

import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.latte.util.Tuple;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class Javascript implements Script {
	private final Logger log;
	private final long lastModified;
	private final Scriptable parent;
	
	private final org.mozilla.javascript.Script script;
	
	public Javascript(Scriptable parent, URL url) throws Exception {
		log = Logger.getLogger(url.getPath());

		URLConnection conn = url.openConnection();		 
		this.lastModified = conn.getLastModified();
		this.parent = parent;
		
		try {
			Context cx = ContextFactory.getGlobal().enterContext();	
			script = cx.compileReader(new InputStreamReader(conn.getInputStream()), url.getPath(), 1, null);
		} finally {
			Context.exit();
		}
	}
	
	public Javascript(Scriptable parent, String content, ScriptLoader loader) throws Exception {
		log = Logger.getLogger("scriptlet");
		this.lastModified = -1;
		this.parent = parent;
		
		try {
			Context cx = ContextFactory.getGlobal().enterContext();	
			script = cx.compileString(content, "scriptlet", 1, null);
		} finally {
			Context.exit();
		}	
	}

	protected Object eval(Context cx, Scriptable scope, Tuple<String, Object>[] env) throws Exception {
		scope.put("log", scope, log);		
		
		if(env != null) {
			for(Tuple<String, Object> binding : env) {
				scope.put(binding.getKey(), scope, binding.getValue());
			}
		}
		
		return script.exec(cx, scope);
	}
	
	public Object eval(Tuple<String, Object>[] env) throws Exception {
		try {
			Context cx = ContextFactory.getGlobal().enterContext();
			Scriptable scope = cx.newObject(parent);
			scope.setParentScope(parent);
			cx.setWrapFactory(new PrimitiveWrapFactory());
			
			return eval(cx, scope, env);
		} finally {
			Context.exit();
		}
	}
	
	public long lastModified() {
		return lastModified;
	}
}
