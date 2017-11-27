package country;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.entity.ContentType;
import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.ConsumerPactRunnerKt;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.PactTestRun;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import pact.utils.FileReader;

public class PactTest {

	@Test
	public void testPact() {
		RequestResponsePact pact = buildPact();
		MockProviderConfig createDefault = MockProviderConfig.createDefault();
		PactVerificationResult result = ConsumerPactRunnerKt.runConsumerTest(pact, createDefault, 
			mockServer -> {
				CountriesPort countriesPort = CountryConfiguration.getCountriesPort(mockServer.getUrl());
				GetCountryRequest request = new GetCountryRequest();
				request.setName("Spain");
				GetCountryResponse response = countriesPort.getCountry(request);
				assertEquals(response.getCountry().getCapital(), "Madrid");
			}
		);
		
		if (result instanceof PactVerificationResult.Error) {
			throw new RuntimeException(((PactVerificationResult.Error) result).getError());
		}
		
		assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	}

	private static RequestResponsePact buildPact() {
		String xmlResponse = FileReader.readFile("ValidSoapResponse.xml", Charset.defaultCharset());
		
		RequestResponsePact pact = ConsumerPactBuilder
				.consumer("Countries consumer")
				.hasPactWith("Countries provider")
				.uponReceiving("a request to retrieve country details")
				.path("/")
				.method("POST")
				.willRespondWith()
				.status(200)
				.body(xmlResponse,
					  ContentType.TEXT_XML)
				.toPact();
		return pact;
	}
}