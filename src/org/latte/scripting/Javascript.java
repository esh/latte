package org.latte.scripting;

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.latte.util.Tuple;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class Javascript implements Script {
	private final static Logger LOG = Logger.getLogger(Javascript.class);
	private final long lastModified;
	private final ScriptLoader loader;
	private final Scriptable parent;
	
	private final org.mozilla.javascript.Script script;
	
	public Javascript(Scriptable parent, File file, ScriptLoader loader) throws Exception {
		this.lastModified = file.lastModified();
		this.loader = loader;
		this.parent = parent;
		
		try {
			Context cx = ContextFactory.getGlobal().enterContext();	
			script = cx.compileReader(new FileReader(file), file.getAbsolutePath(), 1, null);
		} finally {
			Context.exit();
		}
	}
	
	public Javascript(Scriptable parent, String content, ScriptLoader loader) throws Exception {
		this.lastModified = -1;
		this.loader = loader;
		this.parent = parent;
		
		try {
			Context cx = ContextFactory.getGlobal().enterContext();	
			script = cx.compileString(content, "scriptlet", 1, null);
		} finally {
			Context.exit();
		}	
	}

	protected Object eval(Context cx, Scriptable scope, Tuple<String, Object>[] env) {
		scope.put("log", scope, LOG);
		scope.put("require", scope, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				try {
					if(params != null && params.length == 1 && params[0] instanceof String) return ((Javascript)loader.get((String)params[0])).eval(cx, scope, null);
					else throw new IllegalArgumentException("expecting string");
				} catch(Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		scope.put("register", scope, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				if(params != null && params.length == 2 && params[0] instanceof String) {
					parent.put((String) params[0], parent, params[1]);
					return null;
				} else throw new IllegalArgumentException("expecting string, obj");
			}
		});
		scope.put("render", scope, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
				try {
					if(params != null && params.length == 1 && params[0] instanceof String) return ((JHTML)loader.get((String)params[0])).render(cx, scope);
					else throw new IllegalArgumentException("expecting string");
				} catch(Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		
		if(env != null) {
			for(Tuple<String, Object> binding : env) {
				scope.put(binding.getKey(), scope, binding.getValue());
			}
		}
		
		return script.exec(cx, scope);
	}
	
	public Object eval(Tuple<String, Object>[] env) {
		try {
			Context cx = ContextFactory.getGlobal().enterContext();
			Scriptable scope = cx.newObject(parent);

			return eval(cx, scope, env);
		} finally {
			Context.exit();
		}
	}
	
	public long lastModified() {
		return lastModified;
	}
}