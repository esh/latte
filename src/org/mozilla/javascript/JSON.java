package org.mozilla.javascript;


public class JSON {
	public static String stringify(Context cx, Scriptable scope, Scriptable thisObj) {
		return ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, null);
	}
}
