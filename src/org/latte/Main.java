package org.latte;

import org.apache.log4j.PropertyConfigurator;
import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;
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

		// load latte core components
		loader = new ScriptLoader();
		((Javascript)loader.get("autoexec.js")).eval(new Tuple[] {});
	}
}
