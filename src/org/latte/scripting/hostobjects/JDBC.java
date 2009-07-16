package org.latte.scripting.hostobjects;

import java.sql.DriverManager;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class JDBC implements Callable {
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		if(params != null && params.length == 2 && params[0] instanceof String && params[1] instanceof String) {
			try {
				Class.forName((String)params[0]);
				
				return DriverManager.getConnection((String)params[1]);
			} catch (Exception e) {
				throw new JavaScriptException(e.toString(), "jdbc", 0);
			}
			
		}
		else throw new JavaScriptException("expecting 2 strings", "jdbc", 0);
	}
}
