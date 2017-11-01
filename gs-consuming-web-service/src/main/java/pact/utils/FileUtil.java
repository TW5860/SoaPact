package pact.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

	public static String readFile(String path, Charset encoding) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			return "";
		}
		return new String(encoded, encoding);
	}

}
