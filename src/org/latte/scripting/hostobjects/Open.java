package org.latte.scripting.hostobjects;

import java.io.File;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class Open implements Callable {
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		if(params != null && params.length == 1 && params[0] instanceof String) {
			return new FileProxy(new File((String)params[0]), "text");
		}
		else if(params != null && params.length == 2 && params[0] instanceof String && params[1] instanceof String) {
			return new FileProxy(new File((String)params[0]), (String)params[1]);
		}
		else throw new JavaScriptException("expecting string", "open", 0);
	}

}
