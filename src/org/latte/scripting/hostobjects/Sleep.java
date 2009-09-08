package org.latte.scripting.hostobjects;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class Sleep implements Callable {

	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		try {
			long time;
			if(params[0] instanceof Integer) time = ((Integer)params[0]).longValue();
			else if(params[0] instanceof Long) time = ((Long)params[0]).longValue();
			else time = 0;
			
			java.lang.Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new JavaScriptException("sleep", e.toString(), 0);
		}
		
		return null;
	}

}
