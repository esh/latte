package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class HPost implements Callable {

	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		try {
			URLConnection uc = new URL((String)params[0]).openConnection();
			Scriptable reqParams = (Scriptable)params[1];
			if(reqParams != null) {	
				for(Object key : reqParams.getIds()) {
					uc.setRequestProperty(key.toString(), reqParams.get(key.toString(), reqParams).toString());
				}
			}
			uc.setDoOutput(true);
			OutputStream out = uc.getOutputStream();
			Writer writer = new OutputStreamWriter(out);
			writer.write((String)params[2]);
			writer.close();
			out.flush();
			out.close();
			
			InputStream in = uc.getInputStream();
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
			throw new JavaScriptException(e, "hpost", 0);
		}
	}
}
