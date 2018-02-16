package au.com.dius.pact.soap.converter;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Map;

import javax.xml.stream.XMLStreamException;

import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import au.com.dius.pact.consumer.dsl.XMLCompare;

public class JSONConverterTest {
	@Test
	public void xmlToJSON_convertsSimpleXMLToJSON() throws Exception {
		JSONAssert.assertEquals("{a: {b: \"xxx\", c: \"yyy\"}}",
				JSONConverter.xmlToJSON("<a><b>xxx</b><c>yyy</c></a>"), true);
	}
	
	@Test
	public void xmlToJSON_convertsXMLWithNamespacesToJSON() throws Exception {
		JSONAssert.assertEquals("{\"n1___a\": {b: \"xxx\", c: \"yyy\"}}",
				JSONConverter.xmlToJSON("<n1:a xmlns:n1=\"http://ze/ns1\"><b>xxx</b><c>yyy</c></n1:a>"), true);
	}

	@Test
	public void xmlToJSON_convertsCustomXMLNamespaces() throws Exception {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://ze/ns1", "NN1");

		JSONAssert.assertEquals("{\"NN1___a\": {b: \"xxx\", c: \"yyy\"}}",
				JSONConverter.xmlToJSON("<n1:a xmlns:n1=\"http://ze/ns1\"><b>xxx</b><c>yyy</c></n1:a>",
								 jsonConfig), true);
	}
	
	@Test
	public void xmlToJSON_convertsXMLWithDefaultNamespaceToJSON() throws Exception {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://ze/ns1", "NN1");

		JSONAssert.assertEquals("{\"NN1___a\": {\"NN1___b\": \"xxx\", \"NN1___c\": \"yyy\"}}",
				JSONConverter.xmlToJSON("<a xmlns=\"http://ze/ns1\"><b>xxx</b><c>yyy</c></a>", jsonConfig), true);
	}
	
	@Test(expected = IllegalStateException.class)
	public void xmlToJSON_failsForXMLWithUnconfiguredDefaultNamespace() throws Exception {
		try {
			JSONConverter.xmlToJSON("<a xmlns=\"http://ze/ns1\"><b>xxx</b><c>yyy</c></a>");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage(), containsString("http://ze/ns1"));
			throw e;
		}
	}

	@Test
	public void xmlToObj_convertsSimpleObjectToJSON() throws Exception {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");

		JSONAssert.assertEquals("{getCountryRequest: {name: \"Spain\"}}",
				JSONConverter.objToJSON(request, GetCountryRequest.class, jsonConfig), true);
	}

	@Test
	public void xmlToObj_convertsSimpleObjectToJSONWithNamespace() throws Exception {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "gd");

		JSONAssert.assertEquals("{\"gd___getCountryRequest\": {\"gd___name\": \"Spain\"}}",
				JSONConverter.objToJSON(request, GetCountryRequest.class, jsonConfig), true);
	}
	
	@Test
	public void jsonToXML_convertsSimpleJSONObjectToXML()
			throws JSONException, XMLStreamException {
		assertThat(JSONConverter.jsonToXML("{a: {b: \"xxx\", c: \"yyy\"}}"),
				XMLCompare.isEquivalentXMLTo("<a><c>yyy</c><b>xxx</b></a>"));
	}

	@Test
	public void jsonToXML_convertsSimpleJSONObjectToXMLWithNamespaces()
			throws JSONException, XMLStreamException {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "gd");

		String actualXML = JSONConverter.jsonToXML("{a: {\"gd___b\": \"xxx\", c: \"yyy\"}}", jsonConfig);
		String expectedXML = "<a xmlns:gd=\"http://spring.io/guides/gs-producing-web-service\"><c>yyy</c><gd:b>xxx</gd:b></a>";
		assertThat(actualXML, XMLCompare.isEquivalentXMLTo(expectedXML));

		namespaces.put("http://kkqq.com", "kkqq");
		actualXML = JSONConverter.jsonToXML("{\"gd___a\": {\"kkqq___b\": \"xxx\", \"kkqq___c\": \"yyy\"}}", jsonConfig);
		expectedXML = "<gd:a xmlns:gd=\"http://spring.io/guides/gs-producing-web-service\" xmlns:kkqq=\"http://kkqq.com\"><kkqq:c>yyy</kkqq:c><kkqq:b>xxx</kkqq:b></gd:a>";
		assertThat(actualXML, XMLCompare.isEquivalentXMLTo(expectedXML));
	}

	@Test(expected = JSONException.class)
	public void jsonToXML_failsForUndeclaredNamespace() throws JSONException, XMLStreamException {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "gd");

		try {
			System.out.println(JSONConverter.jsonToXML("{\"gd___a\": {\"kkqq___b\": \"xxx\", \"kkqq___c\": \"yyy\"}}", jsonConfig));
		} catch (JSONException e) {
			assertThat(e.getMessage(), containsString("kkqq___"));
			throw e;
		}
	}
}
