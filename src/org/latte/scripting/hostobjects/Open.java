package org.latte.scripting.hostobjects;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class Open implements Callable {
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		if(params != null && params.length == 1 && params[0] instanceof String) {
			try {
				return new FileProxy(cx, scope, (String)params[0]);
			} catch(Exception e) {
				return e.getMessage();
			}
			
		} else throw new IllegalArgumentException("expecting string");
	}

}
