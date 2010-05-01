package org.latte.scripting;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.List;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import org.latte.util.Tuple;

public class JHTML implements Script {
	private static final String CLOSE = "%>";
	private static final String OPEN = "<%";
	private static final String OPEN_EQ = "<%=";
	
	private final long lastModified;
	private final Scriptlet[] scriptlets;
	
	private interface Scriptlet {
		public void render(final StringBuilder buffer, Context cx, Scriptable scope, Object params) throws Exception;
	}
	
	private class JSScriptlet implements Scriptlet {
		private Javascript script;

		public JSScriptlet(Javascript script) {
			this.script = script;
		}
		
		@SuppressWarnings("unchecked")
		public void render(final StringBuilder buffer, Context cx, Scriptable scope, Object params) throws Exception {
			script.eval(
				cx,
				scope,
				new Tuple[] {
					new Tuple("params", params),
					new Tuple("echo", new Callable() {
						public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
							try {
								if(params != null && params.length == 1) buffer.append(params[0].toString());
								else throw new IllegalArgumentException("expecting 1 arg");
							
								return null;
							} catch(Exception e) {
								throw new IllegalArgumentException(e);
							}
						}
					})
				});
			}
		}
	
	private class HTMLScriptlet implements Scriptlet {
		private String contents;
		
		public HTMLScriptlet(String contents) {
			this.contents = contents;
		}
		public void render(final StringBuilder buffer, Context cx, Scriptable scope, Object params) {
			buffer.append(contents);
		}
	}
	
	public JHTML(Scriptable parent, URL url, ScriptLoader loader) throws Exception {
		URLConnection conn = url.openConnection();		 
		this.lastModified = conn.getLastModified();

		StringBuilder sb = new StringBuilder();
		InputStream is = conn.getInputStream();
		try {
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) {
			    sb.append(line).append("\n");
			}
		} finally {
			is.close();
		}
		
		List<Scriptlet> t = new ArrayList<Scriptlet>();
		String text = sb.toString();
		
		int start = 0, open = 0, close = 0;		
		while((open = text.indexOf(OPEN, start)) != -1) {
			// write out normal html
			t.add(new HTMLScriptlet(text.substring(start, open)));
			
			if((close = text.indexOf(CLOSE, open)) != -1) {
				if(text.startsWith(OPEN_EQ, open)) {
					// we need to write out this string
					open += OPEN_EQ.length() + 1;
					
					// add a guard for the output
					if(open < close) {
						String script = "var out = " + text.substring(open, close) + "; if(out != null) { echo(out) }";
						t.add(new JSScriptlet(new Javascript(parent, script, loader)));
					}
				} else {
					// handle everything else
				
					open += OPEN.length() + 1;
					
					if(open < close) {
						// extract and run the script
						String script = text.substring(open, close);
						t.add(new JSScriptlet(new Javascript(parent, script, loader)));
					}
				}
				
				// set the new start
				if(close + CLOSE.length() < text.length()) start = close + CLOSE.length();
				else start = text.length();
			} else {
				throw new IllegalStateException("bracket mismatch");
			}
		}
		
		// print the rest of the html
		t.add(new HTMLScriptlet(text.substring(start, text.length())));
		
		scriptlets = t.toArray(new Scriptlet[t.size()]);
	}
	
	public String render(Context cx, Scriptable scope, Object params) throws Exception {
		StringBuilder buffer = new StringBuilder();
		
		// produce the page
		for(Scriptlet scriptlet : scriptlets) {
			scriptlet.render(buffer, cx, scope, params);
		}
		
		return buffer.toString();
	}
	
	public long lastModified() {
		return lastModified;
	}
}
