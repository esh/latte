package org.latte;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;
import org.latte.scripting.hostobjects.ConfigProxy;
import org.latte.util.Tuple;

public class Main  {
	private ScriptLoader loader;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	@SuppressWarnings("unchecked")
	public Main() throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		
		// load config
		Properties config = new Properties();
		config.load(new FileInputStream("latte.properties"));

		// load latte core components
		loader = new ScriptLoader();
		((Javascript)loader.get("autoexec.js")).eval(new Tuple[] { new Tuple<String, Object>("config", new ConfigProxy(config))  });
	}
}
