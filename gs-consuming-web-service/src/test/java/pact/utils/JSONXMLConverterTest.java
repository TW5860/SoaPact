package pact.utils;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JSONXMLConverterTest {
	private String convertJSONToXML(String xml) throws Exception {
		Reader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();

		JSONXMLConverter.xmlToJSON(reader, writer);

		return writer.toString();
	}

	@Test
	public void xmlToJSON_convertsSimpleXMLToJSON() throws Exception {
		JSONAssert.assertEquals("{a: {b: \"xxx\", c: \"yyy\"}}",
				convertJSONToXML("<a><b>xxx</b><c>yyy</c></a>"), true);
	}
}
