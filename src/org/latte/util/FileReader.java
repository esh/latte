package org.latte.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader {
	public static String read(String path) throws IOException {
		File file = new File(path);
		byte[] buffer = new byte[(int)file.length()];
		FileInputStream fin = new java.io.FileInputStream(file);
		fin.read(buffer);
		fin.close();
		
		return new String(buffer);
	}
}
