package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.javascript.Scriptable;

public class HConnectionProxy {
	private URLConnection uc;
	
	public HConnectionProxy(String url, Scriptable params) throws MalformedURLException, IOException {
		uc = new URL(url).openConnection();
		
		if(params != null) {	
			for(Object key : params.getIds()) {
				uc.setRequestProperty(key.toString(), params.get(key.toString(), params).toString());
			}
		}
	}
	
	public void write(String buf) throws IOException {
		uc.setDoOutput(true);
		
		Writer writer = new OutputStreamWriter(uc.getOutputStream());
		writer.write(buf);
		writer.close();
	}
	
	public String read() throws IOException {
		BufferedReader br = new BufferedReader(new java.io.InputStreamReader(uc.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String res;
		while ((res = br.readLine()) != null) {
			sb.append(res);
	 	}
	  	br.close();
	  	
	  	return sb.toString();
	}
}
