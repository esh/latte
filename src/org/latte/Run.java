package org.latte;

import org.apache.log4j.PropertyConfigurator;
import org.latte.scripting.Javascript;
import org.latte.scripting.ScriptLoader;

public class Run {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		
		if(args.length == 1) {
			((Javascript)new ScriptLoader("").get(args[0])).eval(null);
		}
		else if(args.length == 2) {
			((Javascript)new ScriptLoader(args[0].split(";")).get(args[1])).eval(null);
		}
		else {
			System.err.println("expecting 1 or 2 argument(s)");
			System.exit(-1);
		}
	}
}
