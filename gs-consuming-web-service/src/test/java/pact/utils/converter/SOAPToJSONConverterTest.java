package pact.utils.converter;

import static org.junit.Assert.assertThat;

import java.util.Map;

import org.codehaus.jettison.mapped.Configuration;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import pact.utils.FileReader;
import pact.utils.XMLCompare;
import pact.utils.converter.SOAPToJSONConverter;

public class SOAPToJSONConverterTest {
	private Configuration makeTestConfiguration() {
		Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "ct");
		return jsonConfig;
	}

	@Test
	public void soapRequestToJSON_convertsSimpleXMLRequestToJSON() throws Exception {
		Configuration jsonConfig = makeTestConfiguration();

		String soapRequestInXML = FileReader.readFile("ValidSoapRequest.xml");
		String soapRequestInJSON = FileReader.readFile("ValidSoapRequestInJSON.json");
		JSONAssert.assertEquals(soapRequestInJSON,
				SOAPToJSONConverter.soapRequestToJSON(soapRequestInXML, jsonConfig), true);
	}

	@Test
	public void jsonToSoapResponse_convertsSimpleJSONResponseToXML() throws Exception {
		Configuration jsonConfig = makeTestConfiguration();

		String soapResponseInJSON = FileReader.readFile("ValidSoapResponseInJSON.json");
		String soapResponseInXML = FileReader.readFile("ValidSoapResponse.xml");
		String soapResponseXMLText = SOAPToJSONConverter.jsonToSoapResponse(soapResponseInJSON, jsonConfig);
		assertThat(soapResponseXMLText, XMLCompare.isEquivalentXMLTo(soapResponseInXML));
	}
}
