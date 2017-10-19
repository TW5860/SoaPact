package country;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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

public class PactTest {

	@Test
	public void testPact() {
		RequestResponsePact pact = ConsumerPactBuilder
				.consumer("Countries consumer")
				.hasPactWith("Countries provider")
				.uponReceiving("a request to retrieve a country")
				.path("/")
				.method("POST")
				.willRespondWith()
				.status(200)
				.body("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
						"<SOAP-ENV:Header/>" +
						"  <SOAP-ENV:Body>" +
						"    <ns2:getCountryResponse xmlns:ns2=\"http://spring.io/guides/gs-producing-web-service\">" +
						"      <ns2:country>" +
						"        <ns2:name>Spain</ns2:name>" +
						"        <ns2:population>46704314</ns2:population>" +
						"        <ns2:capital>Madrid</ns2:capital>" +
						"        <ns2:currency>EUR</ns2:currency>" +
						"      </ns2:country>" +
						"    </ns2:getCountryResponse>" +
						"  </SOAP-ENV:Body>" +
						"</SOAP-ENV:Envelope>",
						ContentType.TEXT_XML)
				.toPact();

		MockProviderConfig config = MockProviderConfig.createDefault();
		PactVerificationResult result = ConsumerPactRunnerKt.runConsumerTest(pact, config, new PactTestRun() {
			@Override
			public void run(MockServer mockServer) throws IOException {
				CountriesPort countriesPort = CountryConfiguration.getCountriesPort(mockServer.getUrl());
				GetCountryRequest request = new GetCountryRequest();
				request.setName("Spain");
				GetCountryResponse response = countriesPort.getCountry(request);
				assertEquals(response.getCountry().getCapital(), "Madrid");
			}
		});
		
		if (result instanceof PactVerificationResult.Error) {
			throw new RuntimeException(((PactVerificationResult.Error) result).getError());
		}
		
		assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	}
}