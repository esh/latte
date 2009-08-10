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
		if(args.length != 1) {
			System.err.println("expecting 1 argument");
			System.exit(-1);
		}
		PropertyConfigurator.configure("log4j.properties");
		
		((Javascript)new ScriptLoader("").get(args[0])).eval(null);
	}
}
