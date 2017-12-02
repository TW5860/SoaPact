package country;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import pact.utils.PactDslSoapBody;
import pact.utils.proxy.ReverseProxy;
import pact.utils.proxy.SOAPToJSON2WayReverseProxy;

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
		try {
			GetCountryRequest request = new GetCountryRequest();
			request.setName("Spain");
			
			requestForAnExistingCountry = new PactDslSoapBody()
					.withNs("http://spring.io/guides/gs-producing-web-service")
					.fromObject(request, GetCountryRequest.class);
		} catch (JAXBException e) {
			requestForAnExistingCountry = new PactDslJsonBody()
					.object("getCountryRequest")
					.stringValue("name", "Spain")
					.closeObject();
		}
		
		DslPart responseForAnExistingCountry = new PactDslJsonBody()
				.object("getCountryResponse")
					.object("country")
						.stringValue("name", "Spain")
						.integerType("population")
					.closeObject()
				.closeObject();
		
		
		

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
		ReverseProxy proxy = new SOAPToJSON2WayReverseProxy(mockServer.getUrl());
		
		proxy.start();
		try {
			CountriesPort countriesPort = CountryConfiguration.getCountriesPort(proxy.getUrl());
			GetCountryResponse country = countriesPort
					.getCountry(getCounrtyRequestForAnExistingCountry());
			assertEquals(country.getCountry().getName(), "Spain");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			proxy.stop();
		}
	}

	private GetCountryRequest getCounrtyRequestForAnExistingCountry() {
		GetCountryRequest getCountryRequest = new GetCountryRequest();
		getCountryRequest.setName("Spain");
		return getCountryRequest;
	}
}