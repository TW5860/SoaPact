package country;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.model.RequestResponsePact;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

public class PactTest {	
	@Test
	public void xxx() {
		CountriesPort countriesPort = CountryConfiguration.getCountriesFactory("http://localhost:8080/ws/countries.wsdl");
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		GetCountryResponse response = countriesPort.getCountry(request);
		
		assertTrue(response.getCountry().getCapital().equals("Madrid"));
	}
	
	@Test
	public void testPact() {
		RequestResponsePact pact = ConsumerPactBuilder.consumer("Some Consumer").hasPactWith("Some Provider")
				.uponReceiving("a request to say Hello").path("/hello").method("POST").body("{\"name\": \"harry\"}")
				.willRespondWith().status(200).body("{\"hello\": \"harry\"}").toPact();

		// MockProviderConfig config = MockProviderConfig.createDefault();
		// PactVerificationResult result = runConsumerTest(pact, config, new
		// PactTestRun() {
		// @Override
		// public void run(@NotNull MockServer mockServer) throws IOException {
		// Map expectedResponse = new HashMap();
		// expectedResponse.put("hello", "harry");
		// assertEquals(expectedResponse, new
		// CountryClient(mockServer.getUrl()).post("/hello",
		// "{\"name\": \"harry\"}", ContentType.APPLICATION_JSON));
		// }
		// });
		//
		// if (result instanceof PactVerificationResult.Error) {
		// throw new
		// RuntimeException(((PactVerificationResult.Error)result).getError());
		// }
		//
		// assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	}

}