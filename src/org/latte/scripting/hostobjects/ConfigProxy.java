package org.latte.scripting.hostobjects;

import java.util.Properties;
import java.util.Map.Entry;

import org.mozilla.javascript.ScriptableObject;

public class ConfigProxy extends ScriptableObject {
	public ConfigProxy(Properties config) {
		for(Entry<Object, Object> e : config.entrySet()) {
			ScriptableObject.putProperty(this, e.getKey().toString(), e.getValue().toString());
		}
	}
	
	@Override
	public String getClassName() {
		return "config";
	}
}
