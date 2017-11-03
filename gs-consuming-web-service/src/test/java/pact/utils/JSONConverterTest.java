package pact.utils;

import static org.junit.Assert.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.matchers.CompareMatcher;

import io.spring.guides.gs_producing_web_service.GetCountryRequest;

public class JSONConverterTest {
	public static CompareMatcher isEquivalentXMLTo(String actualXml) {
	     return CompareMatcher.isSimilarTo(actualXml)
	    		 .throwComparisonFailure()
	    		 .normalizeWhitespace()
	    		 .ignoreComments()
	    		 .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText));
	}
	

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
	
	private <S, T extends S> String objToJSON(T obj, Class<S> cls,
			Configuration jsonConfig) throws Exception {
		StringWriter writer = new StringWriter();
		JSONConverter.objToJSON(obj, cls, writer, jsonConfig);
		return writer.toString();
	}
	
	private String jsonToXML(String json) throws JSONException, XMLStreamException {
		StringWriter writer = new StringWriter();
		JSONConverter.jsonToXML(json, writer);
		return writer.toString();
	}

	private String jsonToXML(String json, Configuration jsonConfig) throws JSONException, XMLStreamException {
		StringWriter writer = new StringWriter();
		JSONConverter.jsonToXML(json, writer, jsonConfig);
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
	
	@Test
	public void xmlToObj_convertsSimpleObjectToJSON() throws Exception {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");

		JSONAssert.assertEquals("{getCountryRequest: {name: \"Spain\"}}",
				objToJSON(request, GetCountryRequest.class, jsonConfig), true);
	}

	@Test
	public void xmlToObj_convertsSimpleObjectToJSONWithNamespace() throws Exception {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "gd");

		JSONAssert.assertEquals("{\"gd#getCountryRequest\": {\"gd#name\": \"Spain\"}}",
				objToJSON(request, GetCountryRequest.class, jsonConfig), true);
	}
	
	@Test
	public void jsonToXML_convertsSimpleJSONObjectToXML()
			throws JSONException, XMLStreamException {
		assertThat(jsonToXML("{a: {b: \"xxx\", c: \"yyy\"}}"),
				isEquivalentXMLTo("<a><c>yyy</c><b>xxx</b></a>"));
	}
}
