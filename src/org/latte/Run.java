package org.latte;

import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;
import org.latte.scripting.hostobjects.HTTPServer;

public class Run {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String script = null;
		
		if(args.length == 1) {
			script = args[0];
		}
		else if(args.length == 2) {
			script = args[0];
		}
		else {
			System.err.println("usage: org.latte.Run script [args]");
			System.exit(-1);
		}

		ScriptLoader loader  = new ScriptLoader();
		loader.register("httpserver", new HTTPServer());	

		((Javascript)loader.get(script)).eval(null);
	}
}
