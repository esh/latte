package org.latte.scripting.hostobjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.mortbay.jetty.Request;
import org.mozilla.javascript.ScriptableObject;

public class RequestProxy extends ScriptableObject {
	private class Params extends ScriptableObject {
		protected Params(HttpServletRequest request) {			
			if(request.getParameterMap().size() > 0) {
				for(String key : (Set<String>)request.getParameterMap().keySet()) {
					Object value = request.getParameterMap().get(key);
					
					if(request.getContentType().startsWith("multipart/form-data")) {
						if(value instanceof String) {
							File tmp = (File)request.getAttribute(key);	
							File file = new File(tmp.getAbsoluteFile() + ((String)value).substring(((String)value).indexOf(".")));
							tmp.renameTo(file);
							ScriptableObject.putProperty(this, key, file.getAbsolutePath());
						}
						else {
							ScriptableObject.putProperty(this, key, new String((byte[])value));
						}
					}
					else {
						ScriptableObject.putProperty(this, key, ((String[])value)[0]);
					}
				}
			}
		}
		
		@Override
		public String getClassName() {
			return "Params";
		}
		
	}
	
	public RequestProxy(HttpServletRequest request) throws IOException {
		ScriptableObject.putProperty(this, "hostname", InetAddress.getByName(request.getRemoteAddr()).getHostName());
		ScriptableObject.putProperty(this, "url", request.getRequestURI());
		ScriptableObject.putProperty(this, "params", new Params(request));	
		
		String authorization = (String)request.getHeader("Authorization");
		if(authorization != null && authorization.startsWith("Basic"))  ScriptableObject.putProperty(this, "authorization", new String(new Base64().decode(authorization.split(" ")[1].getBytes())));
		
		if("application/json".equals(request.getContentType())) {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			ScriptableObject.putProperty(this, "content", sb.toString());
		}
	}
	
	@Override
	public String getClassName() {
		return "Request";
	}

}
