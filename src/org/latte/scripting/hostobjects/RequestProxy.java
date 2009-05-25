package org.latte.scripting.hostobjects;

import java.io.File;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
	
	public RequestProxy(HttpServletRequest request) {
		ScriptableObject.putProperty(this, "url", request.getRequestURI());
		ScriptableObject.putProperty(this, "params", new Params(request));
	}
	
	@Override
	public String getClassName() {
		return "Request";
	}

}
