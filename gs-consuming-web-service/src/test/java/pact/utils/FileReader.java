package pact.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {
	
	public static String readFile(String filename) {
		return readFile(filename, Charset.defaultCharset());
	}

	public static String readFile(String filename, Charset encoding) {
		ClassLoader classLoader = FileReader.class.getClassLoader();
		String path = classLoader.getResource(filename).getPath();
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new String(encoded, encoding);
	}

}
