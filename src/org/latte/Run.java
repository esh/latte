package org.latte;

import org.apache.log4j.PropertyConfigurator;
import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;
import org.latte.scripting.hostobjects.HTTPServer;

public class Run {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		String[] paths = null;
		String script = null;
		
		if(args.length == 1) {
			paths = new String[0];
			script = args[0];
		}
		else if(args.length == 2) {
			paths = args[0].split(":");
			script = args[1];
		}
		else {
			System.err.println("usage: org.latte.Run path[:paths] script");
			System.exit(-1);
		}

		ScriptLoader loader  = new ScriptLoader(paths);
		loader.register("httpserver", new HTTPServer());	

		((Javascript)loader.get(script)).eval(null);
	}
}
