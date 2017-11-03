package pact.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import country.CountryConfiguration;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import pact.utils.JSONMLConvertingReverseProxy;
import pact.utils.StaticBackendServer;

public class ReverseProxySoapTest {

	@Test
	public void shouldCreateValidSoapResponsesFromJSONML() {
		// PREPARE
		// WS CLIENT
		CountriesPort countriesPort = CountryConfiguration.getCountriesPort("http://localhost:8080");
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");

		// MOCK WS
		String staticResponse = "{\"xmlns:SOAP-ENV\":\"http://schemas.xmlsoap.org/soap/envelope/\",\"childNodes\":[{\"tagName\":\"SOAP-ENV:Header\"},{\"childNodes\":[{\"xmlns:ns2\":\"http://spring.io/guides/gs-producing-web-service\",\"childNodes\":[{\"childNodes\":[{\"childNodes\":[\"Spain\"],\"tagName\":\"ns2:name\"},{\"childNodes\":[46704314],\"tagName\":\"ns2:population\"},{\"childNodes\":[\"Madrid\"],\"tagName\":\"ns2:capital\"},{\"childNodes\":[\"EUR\"],\"tagName\":\"ns2:currency\"}],\"tagName\":\"ns2:country\"}],\"tagName\":\"ns2:getCountryResponse\"}],\"tagName\":\"SOAP-ENV:Body\"}],\"tagName\":\"SOAP-ENV:Envelope\"}";
		StaticBackendServer jsonSoapService = new StaticBackendServer("localhost", 9999, staticResponse);
		jsonSoapService.start();
		JSONMLConvertingReverseProxy proxy = new JSONMLConvertingReverseProxy("localhost", 8080, "http://localhost:9999");
		proxy.start();
		
		// ACT
		GetCountryResponse response = countriesPort.getCountry(request);
		
		// VERIFY
		assertEquals(response.getCountry().getCapital(), "Madrid");
	}
}
