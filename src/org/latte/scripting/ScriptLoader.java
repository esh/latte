package org.latte.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class ScriptLoader {
	private static final Logger LOG = Logger.getLogger(ScriptLoader.class);
	
	private final String[] paths;
	private final Scriptable parent;
	private final Map<String, Script> mapping = new HashMap<String, Script>();
	
	public ScriptLoader() {
		this(".");
	}
	
	public ScriptLoader(String path) {
		this(new String[] { path });
	}
	
	public ScriptLoader(String[] paths) {
		for(int i = 0 ; i < paths.length ; i++) {
			if(paths[i].length() > 0 && !paths[i].endsWith("/")) paths[i] += "/";
		}
		
		this.paths = paths;
		Context cx = ContextFactory.getGlobal().enterContext();
		this.parent = cx.initStandardObjects(null, false);
		Context.exit();
	}
	
	public Script get(String p) throws Exception {
		Script script = mapping.get(p);
		for(String path : paths) {
			try {
				File file = new File(path + p);
				if(script == null || script.lastModified() < file.lastModified()) {
					if(p.endsWith(".js")) script = new Javascript(parent, file, this);
					else script = new JHTML(parent, file, this);
					mapping.put(p, script);
					
					LOG.info("loaded: " + p);
					return script;
				}
				else if(script != null) {
					return script;
				}
			} catch(FileNotFoundException e) {}
		}
		
		throw new FileNotFoundException(p);
	}
}
