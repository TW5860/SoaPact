package pact.utils;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jettison.mapped.Configuration;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JSONConverterTest {
	private String xmlToJSON(String xml) throws Exception {
		Reader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();

		JSONConverter.xmlToJSON(reader, writer);

		return writer.toString();
	}

	private String xmlToJSON(String xml, Configuration jsonConfig) throws Exception {
		Reader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();

		JSONConverter.xmlToJSON(reader, writer, jsonConfig);

		return writer.toString();
	}

	@Test
	public void xmlToJSON_convertsSimpleXMLToJSON() throws Exception {
		JSONAssert.assertEquals("{a: {b: \"xxx\", c: \"yyy\"}}",
				xmlToJSON("<a><b>xxx</b><c>yyy</c></a>"), true);
	}
	
	@Test
	public void xmlToJSON_convertsXMLWithNamespacesToJSON() throws Exception {
		JSONAssert.assertEquals("{\"n1#a\": {b: \"xxx\", c: \"yyy\"}}",
				xmlToJSON("<n1:a xmlns:n1=\"http://ze/ns1\"><b>xxx</b><c>yyy</c></n1:a>"), true);
	}

	@Test
	public void xmlToJSON_convertsCustomXMLNamespaces() throws Exception {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://ze/ns1", "NN1");

		JSONAssert.assertEquals("{\"NN1#a\": {b: \"xxx\", c: \"yyy\"}}",
				xmlToJSON("<n1:a xmlns:n1=\"http://ze/ns1\"><b>xxx</b><c>yyy</c></n1:a>",
								 jsonConfig), true);
	}
}
