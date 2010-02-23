package org.latte.scripting.hostobjects;

import java.net.InetAddress;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class GetHostByAddr implements Callable {
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		try {
			return InetAddress.getByName((String)params[0]).getHostName();
		} catch(Exception e) {
			throw new JavaScriptException(e, "gethostbyaddr", 0);
		}
	}
}
