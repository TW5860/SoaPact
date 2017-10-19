package country;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONML;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;

public class ConverterTest {

	@Test
	public void shouldConvertFromSimpleXmlToJson() {
		String json = XML2JSONConvertingReverseProxy.xmlToJSON("<xml>hallo</xml>");
		assertEquals(json, "{\"xml\":\"hallo\"}");
	}

	@Test
	public void shouldKeepXmlTagsToJson() {
		String json = XML2JSONConvertingReverseProxy.xmlToJSON("<xml src=\"hallo\">hallo</xml>");
		assertEquals(json, "{\"xml\":{\"src\":\"hallo\",\"content\":\"hallo\"}}");
	}

	static String readFile(String path, Charset encoding) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			return "";
		}
		return new String(encoded, encoding);
	}

	@Test
	public void conversionToJSONAndBackToXMLShouldNotResultInChange() {
		ClassLoader classLoader = getClass().getClassLoader();
		String path = classLoader.getResource("ValidSoapRequest.xml").getPath();
		String xml = readFile(path, Charset.defaultCharset());
		JSONObject jsonObject = JSONML.toJSONObject(xml);
		String xmlConverted = JSONML.toString(jsonObject);
		assertEquals(xml,xmlConverted);
	}
}
