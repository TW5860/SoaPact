package au.com.dius.pact.soap.converter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import au.com.dius.pact.consumer.dsl.FileReader;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import au.com.dius.pact.consumer.dsl.XMLCompare;

public class SOAPToJSONConverterTest {
	@Test
	public void namespaceMapForXML_createsAMapWithHashedUris() {
		String soapRequestInXML = FileReader.readFile("ValidSoapRequest.xml");
		Map<String, String> expected = new HashMap<>();
		expected.put("http://schemas.xmlsoap.org/soap/envelope/", "Byxibe");
		expected.put("http://spring.io/guides/gs-producing-web-service", "Je");
		assertThat(SOAPToJSONConverter.namespaceMapForXML(soapRequestInXML),
				equalTo(expected));
	}
	
	@Test
	public void soapRequestToJSON_convertsSimpleXMLRequestToJSON() throws Exception {
		String soapRequestInXML = FileReader.readFile("ValidSoapRequest.xml");
		String soapRequestInJSON = FileReader.readFile("ValidSoapRequestInJSON.json");
		JSONAssert.assertEquals(soapRequestInJSON,
				SOAPToJSONConverter.soapRequestToJSON(soapRequestInXML), true);
	}

	@Test
	public void jsonToSoapResponse_convertsSimpleJSONResponseToXML() throws Exception {
		String soapResponseInJSON = FileReader.readFile("ValidSoapResponseInJSON.json");
		String soapResponseInXML = FileReader.readFile("ValidSoapResponse.xml");
		String soapResponseXMLText = SOAPToJSONConverter.jsonToSoapResponse(soapResponseInJSON);
		assertThat(soapResponseXMLText, XMLCompare.isEquivalentXMLTo(soapResponseInXML));
	}
}
