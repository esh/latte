package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class Shell implements Callable {
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		if(params != null && params.length == 1 && params[0] instanceof String) {
			try {
				Process p = Runtime.getRuntime().exec((String)params[0]);
				
				StringBuilder sb = new StringBuilder();
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				
				String t1 = null,t2 = null;
				while((t1 = in.readLine()) != null || (t2 = err.readLine()) != null) {
					if(t1 != null) {
						sb.append(t1);
						sb.append("\n");
					}
					if(t2 != null) {
						sb.append(t2);
						sb.append("\n");
					}
				}
				
				return sb.toString();
			} catch(Exception e) {
				return e.getMessage();
			}
			
		} else throw new IllegalArgumentException("expecting string");
	}
}
