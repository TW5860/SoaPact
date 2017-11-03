package country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.json.JSONML;
import org.json.JSONObject;
import org.junit.Test;

import pact.utils.FileUtils;
import pact.utils.JSONConverter;
import pact.utils.JSONMLConverter;

public class ConverterTest {

	@Test
	public void shouldConvertFromSimpleXmlToJson() {
		String json = JSONMLConverter.xmlToJSON("<xml>hallo</xml>");
		assertEquals(json, "{\"childNodes\":[\"hallo\"],\"tagName\":\"xml\"}");
	}

	@Test
	public void shouldKeepXmlTagsToJson() {
		String json = JSONMLConverter.xmlToJSON("<xml src=\"hallo\">hallo</xml>");
		assertEquals(json, "{\"src\":\"hallo\",\"childNodes\":[\"hallo\"],\"tagName\":\"xml\"}");
	}
	
	@Test
	public void conversionToJSONAndBackToXMLShouldNotResultInChange() {
		String xml = FileUtils.readFile("ValidSoapRequest.xml", Charset.defaultCharset());
		JSONObject jsonObject = JSONML.toJSONObject(xml);
		String xmlConverted = JSONML.toString(jsonObject);
		assertEquals(xml,xmlConverted);
	}
	
	@Test
	public void testResponseAsJSON() {
		String xml = FileUtils.readFile("ValidSoapResponse.xml", Charset.defaultCharset());
		JSONObject jsonObject = JSONML.toJSONObject(xml);
		assertEquals(jsonObject.toString(),"{\"xmlns:SOAP-ENV\":\"http://schemas.xmlsoap.org/soap/envelope/\",\"childNodes\":[{\"tagName\":\"SOAP-ENV:Header\"},{\"childNodes\":[{\"xmlns:ns2\":\"http://spring.io/guides/gs-producing-web-service\",\"childNodes\":[{\"childNodes\":[{\"childNodes\":[\"Spain\"],\"tagName\":\"ns2:name\"},{\"childNodes\":[46704314],\"tagName\":\"ns2:population\"},{\"childNodes\":[\"Madrid\"],\"tagName\":\"ns2:capital\"},{\"childNodes\":[\"EUR\"],\"tagName\":\"ns2:currency\"}],\"tagName\":\"ns2:country\"}],\"tagName\":\"ns2:getCountryResponse\"}],\"tagName\":\"SOAP-ENV:Body\"}],\"tagName\":\"SOAP-ENV:Envelope\"}");
		
	}
}
