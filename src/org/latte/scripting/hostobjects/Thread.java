package org.latte.scripting.hostobjects;

import org.latte.scripting.PrimitiveWrapFactory;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ContextFactory;

public class Thread implements Callable {
	private class ThreadHelper extends java.lang.Thread {
		private ThreadHelper(final Callable fn, final Scriptable scope) {
			super(new Runnable() {
				public void run() {
                    try {
                        Context cx = ContextFactory.getGlobal().enterContext();
			cx.setWrapFactory(new PrimitiveWrapFactory());
                        fn.call(cx, scope, scope, null);
                    } finally {
                        Context.exit();
                    }
				}
			});	
		}
	}
	
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] params) {
		new ThreadHelper((Callable)params[0], scope).start();
		return null;
	}

}
