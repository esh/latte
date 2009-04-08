package org.latte.scripting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class ScriptCache {
	private final Scriptable parent;
	private final Map<String, Script> mapping = new HashMap<String, Script>();
	
	public ScriptCache() {
		Context cx = ContextFactory.getGlobal().enterContext();	
		this.parent = cx.initStandardObjects(null, false);
		Context.exit();
	}
	
	public Script get(String path) throws Exception {
		path = "app/" + path;
		Script script = mapping.get(path);
		if(script == null || script.lastModified() < new File(path).lastModified()) {
			if(path.endsWith(".js")) script = new Javascript(parent, new File(path), this);
			else script = new JHTML(parent, new File(path), this);
			mapping.put(path, script);
		}
		
		return script;
	}
}
