package country;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.codehaus.jettison.mapped.Configuration;

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
import pact.utils.converter.SOAPToJSONConverter;
import pact.utils.proxy.SOAPToJSONReverseProxy;

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
		Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");
		
		SOAPToJSONReverseProxy.runTest(mockServer.getUrl(),jsonConfig , p -> {
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