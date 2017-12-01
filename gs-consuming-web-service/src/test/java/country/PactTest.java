package country;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.codehaus.jettison.mapped.Configuration;
import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.ConsumerPactRunnerKt;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import pact.utils.FileReader;
import pact.utils.SOAPToJSONConverter;
import pact.utils.SOAPToJSONReverseProxy;

public class PactTest {

	@Test
	public void testPact() {
		RequestResponsePact pact = buildPact();
		MockProviderConfig createDefault = MockProviderConfig.createDefault();

		Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "ct");

		PactVerificationResult result = ConsumerPactRunnerKt.runConsumerTest(pact, createDefault, pactServer -> {
			SOAPToJSONReverseProxy.runTest(pactServer.getUrl(), jsonConfig, pactSOAPServer -> {
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
		String jsonResponse = FileReader.readFile("ValidSoapResponseInJSON.json");
		
		RequestResponsePact pact = ConsumerPactBuilder
				.consumer("Countries consumer")
				.hasPactWith("Countries provider")
				.uponReceiving("a request to retrieve country details")
				.path("/")
				.method("POST")
				.willRespondWith()
				.status(200)
				.body(jsonResponse,
					  ContentType.APPLICATION_JSON)
				.toPact();
		return pact;
	}
}