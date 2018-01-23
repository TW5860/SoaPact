package country;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.Country;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import au.com.dius.pact.consumer.dsl.PactDslSoapBody;
import au.com.dius.pact.soap.proxy.SOAPToJSONReverseProxy;

public class AnnotationPactTest extends ConsumerPactTestMk2 {

	@Override
	protected String consumerName() {
		return "Country-Consumer";
	}

	@Override
	protected String providerName() {
		return "Country-Data-Provider";
	}

	@Override
	protected RequestResponsePact createPact(PactDslWithProvider builder) {
		DslPart requestForAnExistingCountry;
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		requestForAnExistingCountry = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service")
				.fromObject(request, GetCountryRequest.class);

		Country country = new Country();
		country.setName("Spain");
		country.setCapital("Madrid");
		GetCountryResponse response = new GetCountryResponse();
		response.setCountry(country);

		// From Object
		DslPart responseForAnExistingCountry = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service")
				.fromObject(response, GetCountryResponse.class);

		// As SoapBody Object
		// TODO: Enable type matching without specific values like:	.numberType("population")
		responseForAnExistingCountry = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service")
				.object("getCountryResponse")
					.object("country")
						.stringType("name","Spain")
						.stringType("capital", "Madrid")
				.closeObject()
			.closeObject()
        .close();

		return builder.given("provider is available") // NOTE: Using provider states are optional, you can leave it out
				.uponReceiving("A request for an existing country").path("/").method("POST")
					.body(requestForAnExistingCountry)
				.willRespondWith()
					.status(200)
					.body(responseForAnExistingCountry)
				.toPact();
	}

	@Override
	protected void runTest(MockServer mockServer) throws IOException {
		SOAPToJSONReverseProxy.runTest(mockServer.getUrl(), p -> {
			CountriesPort countriesPort = CountryConfiguration.getCountriesPort(p.getUrl());
			GetCountryResponse country = countriesPort
					.getCountry(getCounrtyRequestForAnExistingCountry());
			assertEquals(country.getCountry().getName(), "Spain");
		});
	}

	private GetCountryRequest getCounrtyRequestForAnExistingCountry() {
		GetCountryRequest getCountryRequest = new GetCountryRequest();
		getCountryRequest.setName("Spain");
		return getCountryRequest;
	}
}