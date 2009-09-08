package org.latte.scripting.hostobjects;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class Thread implements Callable {
	private class ThreadHelper extends java.lang.Thread {
		private ThreadHelper(final Callable fn, final Context cx, final Scriptable scope) {
			super(new Runnable() {
				public void run() {
					fn.call(cx, scope, scope, null);
				}
			});	
		}
	}
	
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		new ThreadHelper((Callable)params[0], cx, scope).start();
		return null;
	}

}
