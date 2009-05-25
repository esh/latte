package org.latte.scripting.hostobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.mozilla.javascript.JavaScriptException;

public class FileProxy {
	private final File file;
	
	public FileProxy(File file) {
		this.file = file;
	}
	
	public void write(String buf) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(file));
		out.print(buf);
		out.close();	
	}

	public String read() throws Exception {
		if(!file.exists()) throw new JavaScriptException(file.getAbsoluteFile() + " not found", "open", 0);	
		
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
