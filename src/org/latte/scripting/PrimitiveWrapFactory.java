package org.latte.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class PrimitiveWrapFactory extends WrapFactory {

	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class staticType) {
		if (obj instanceof String || obj instanceof Number ||
				obj instanceof Boolean)
				{
				 return obj;
				     } else if (obj instanceof Character) {
				      char[] a = { ((Character)obj).charValue() };
				       return new String(a);
				    }
				 return super.wrap(cx, scope, obj, staticType);
	}

}
