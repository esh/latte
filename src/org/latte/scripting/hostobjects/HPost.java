package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.HttpURLConnection;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class HPost implements Callable {

	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		try {
			URL url = new URL((String)params[0]);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			Scriptable reqParams = (Scriptable)params[1];
			if(reqParams != null) {	
				for(Object key : reqParams.getIds()) {
					connection.setRequestProperty(key.toString(), reqParams.get(key.toString(), reqParams).toString());
				}
			}

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write((String)params[2]);
			writer.close();

			InputStream in = connection.getInputStream();
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
