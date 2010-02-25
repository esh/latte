package org.mozilla.javascript;

import java.io.Reader;
import java.io.IOException;

public class JSON {
	public static String stringify(Context cx, Scriptable scope, Scriptable thisObj) {
		return ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, null);
	}

	public static Object fromString(Context cx, Scriptable scope, String source) {
		return cx.evaluateString(scope, source, "", 0, null);
	}
}
