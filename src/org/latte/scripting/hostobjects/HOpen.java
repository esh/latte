package org.latte.scripting.hostobjects;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class HOpen implements Callable {

	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		if(params != null && params[0] instanceof String) {
			try {
				return new HConnectionProxy((String)params[0], params.length == 2 && params[1] instanceof Scriptable ? (Scriptable)params[1] : null);
			} catch (Exception e) {
				throw new JavaScriptException(e.toString(), "hopen", 0);
			}
		} else throw new JavaScriptException("expecting string", "hopen", 0);
	}

}
