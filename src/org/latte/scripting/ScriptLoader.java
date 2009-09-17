package org.latte.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.latte.scripting.hostobjects.HGet;
import org.latte.scripting.hostobjects.HPost;
import org.latte.scripting.hostobjects.HTTPServer;
import org.latte.scripting.hostobjects.JDBC;
import org.latte.scripting.hostobjects.Open;
import org.latte.scripting.hostobjects.RWLock;
import org.latte.scripting.hostobjects.Shell;
import org.latte.scripting.hostobjects.Sleep;
import org.latte.scripting.hostobjects.Thread;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptLoader {
	private static final Logger LOG = Logger.getLogger(ScriptLoader.class);
	
	private final String[] paths;
	private final Scriptable parent;
	private final Map<String, Script> mapping = new HashMap<String, Script>();
	
	public ScriptLoader() throws Exception {
		this(".");
	}
	
	public ScriptLoader(String path) throws Exception {
		this(new String[] { path });
	}
	
	public ScriptLoader(String[] paths) throws Exception {
		for(int i = 0 ; i < paths.length ; i++) {
			if(paths[i].length() > 0 && !paths[i].endsWith("/")) paths[i] += "/";
		}
		
		this.paths = paths;
		Context cx = ContextFactory.getGlobal().enterContext();
		this.parent = cx.initStandardObjects(null, false);
		
		
		this.parent.put("require", this.parent, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				try {
					if(params != null && params.length == 1 && params[0] instanceof String) return ((Javascript)get((String)params[0])).eval(cx, scope, null);
					else throw new IllegalArgumentException("expecting string");
				} catch(Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		this.parent.put("register", this.parent, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				if(params != null && params.length == 2 && params[0] instanceof String) {
					parent.put((String) params[0], parent, params[1]);
					return null;
				} else throw new IllegalArgumentException("expecting string, obj");
			}
		});
		this.parent.put("render", this.parent, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				try {
					if(params != null && params.length == 2) return ((JHTML)get((String)params[0])).render(cx, scope, params[1]);
					else throw new IllegalArgumentException("expecting 2 args");
				} catch(Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		this.parent.put("thread", this.parent, new Thread());
		this.parent.put("sleep", this.parent, new Sleep());
		this.parent.put("shell", this.parent, new Shell());
		this.parent.put("open", this.parent, new Open());
		this.parent.put("hget", this.parent, new HGet());
		this.parent.put("hpost", this.parent, new HPost());
		this.parent.put("jdbc", this.parent, new JDBC());
		this.parent.put("httpserver", this.parent, new HTTPServer());	
	
		ScriptableObject.defineClass(this.parent, RWLock.class);
			
		Context.exit();
	}
	
	public Script get(String p) throws Exception {
		Script script = mapping.get(p);
		for(String path : paths) {
			try {
				File file = new File(path + p);
				if(script == null || script.lastModified() < file.lastModified()) {
					if(p.endsWith(".js")) script = new Javascript(parent, file);
					else script = new JHTML(parent, file, this);
					mapping.put(p, script);
					
					LOG.info("loaded: " + p);
					return script;
				}
				else if(script != null) {
					return script;
				}
			} catch(FileNotFoundException e) {}
		}
		
		throw new FileNotFoundException(p);
	}
}
