package country;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.ConsumerPactRunnerKt;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.Country;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import pact.utils.PactDslSoapBody;
import pact.utils.proxy.SOAPToJSONReverseProxy;

public class PactTest {

	@Test
	public void testPact() {
		RequestResponsePact pact = buildPact();
		MockProviderConfig createDefault = MockProviderConfig.createDefault();

		PactVerificationResult result = ConsumerPactRunnerKt.runConsumerTest(pact, createDefault, pactServer -> {
			SOAPToJSONReverseProxy.runTest(pactServer.getUrl(), pactSOAPServer -> {
				CountriesPort countriesPort = CountryConfiguration.getCountriesPort(pactSOAPServer.getUrl());
				GetCountryRequest request = new GetCountryRequest();
				request.setName("Spain");
				GetCountryResponse response = countriesPort.getCountry(request);
				assertEquals(response.getCountry().getCapital(), "Madrid");
			});
		});
		
		if (result instanceof PactVerificationResult.Error) {
			throw new RuntimeException(((PactVerificationResult.Error) result).getError());
		}
		
		assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	}

	private static RequestResponsePact buildPact() {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		PactDslSoapBody requestForAnExistingCountry = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service", "Je")
				.fromObject(request, GetCountryRequest.class);
		
		Country country = new Country();
		country.setName("Spain");
		country.setCapital("Madrid");
		GetCountryResponse response = new GetCountryResponse();
		response.setCountry(country);
		PactDslSoapBody responseForAnExistingCountry = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service", "Je")
				.fromObject(response, GetCountryResponse.class);
	
		return ConsumerPactBuilder
				.consumer("Countries consumer")
				.hasPactWith("Countries provider")
				.uponReceiving("A request for an existing country")
					.path("/")
					.method("POST")
					.body(requestForAnExistingCountry)
				.willRespondWith()
					.status(200)
					.body(responseForAnExistingCountry)
				.toPact();
	}
}