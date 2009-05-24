package org.latte.scripting.hostobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class FileProxy {
	private final File file;
	private final Context cx;
	private final Scriptable scope;
	
	public FileProxy(Context cx, Scriptable scope, String path) {
		this.file = new File(path);
		this.cx = cx;
		this.scope = scope;
	}
	
	public void write(String buf) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(file));
		out.print(buf);
		out.close();	
	}

	public String read() throws IOException {
		byte[] buffer = new byte[(int)file.length()];
		FileInputStream in = new java.io.FileInputStream(file);
		in.read(buffer);
		in.close();
		
		return new String(buffer);
	}
	
	public void remove() throws IOException {
		file.delete();
	}
}
