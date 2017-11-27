package pact.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import country.CountryConfiguration;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

public class ReverseProxyTest {

	@Test
	public void shouldPassAnXMLResponseUnmodified() {
		// PREPARE

		// MOCK WS
		String staticResponse = FileReader.readFile("ValidSoapResponse.xml");
		StaticBackendServer jsonSoapService = new StaticBackendServer("localhost", 9999, staticResponse);
		jsonSoapService.start();

		ReverseProxy proxy = new ReverseProxy("localhost", 8080, "http://localhost:9999");
		proxy.start();
		
		// ACT
		CountriesPort countriesPort = CountryConfiguration.getCountriesPort("http://localhost:8080");
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		GetCountryResponse response = countriesPort.getCountry(request);
		
		// VERIFY
		assertEquals(response.getCountry().getCapital(), "Madrid");
	}
}
