package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class Shell implements Callable {
	private static String streamToString(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String t = null;
		while((t = br.readLine()) != null) {
			sb.append(t);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		if(params != null && params.length == 1 && params[0] instanceof String) {
			try {
				Process p = Runtime.getRuntime().exec((String)params[0]);

				String out = streamToString(p.getInputStream());
				String err = streamToString(p.getErrorStream());
				if(err != null && err.length() > 0) throw new Exception(err);
				
				return out;
			} catch(Exception e) {
				throw new JavaScriptException(e, "shell", 0);
			}
			
		} else throw new JavaScriptException("expecting string", "shell", 0);
	}
}
