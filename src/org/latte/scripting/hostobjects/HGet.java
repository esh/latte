package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class HGet implements Callable {

	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		try {
			InputStream in = new URL((String)params[0]).openConnection().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String res;
			while ((res = br.readLine()) != null) {
				sb.append(res);
		 	}
		  	br.close();
		  	in.close();
		  	
			return sb.toString();
		} catch (Exception e) {
			throw new JavaScriptException("hget", e.toString(), 0);
		}
	}
}
