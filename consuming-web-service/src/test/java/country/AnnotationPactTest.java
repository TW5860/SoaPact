package country;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslSoapBody;
import au.com.dius.pact.consumer.dsl.PactDslSoapBody2;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import au.com.dius.pact.soap.proxy.SOAPToJSONReverseProxy;
import io.spring.guides.gs_producing_web_service.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
        ObjectFactory factory = new ObjectFactory();
        DslPart requestForAnExistingCountry;

        GetCountryRequest request = factory.createGetCountryRequest().withName("Spain");
        requestForAnExistingCountry = new PactDslSoapBody()
                .withNs("http://spring.io/guides/gs-producing-web-service")
                .fromObject(request, GetCountryRequest.class);

        GetCountryResponse response = factory.createGetCountryResponse()
                .withCountry(factory.createCountry()
                        .withName("Spain")
                        .withCapital("Madrid")
                        .withCurrency(Currency.EUR)
                        .withPopulation(7000));

        // From Object
//		DslPart responseForAnExistingCountry = new PactDslSoapBody()
//				.withNs("http://spring.io/guides/gs-producing-web-service")
//				.fromObject(response, GetCountryResponse.class);

        PactDslSoapBody2 responseForAnExistingCountry = new PactDslSoapBody2();
        responseForAnExistingCountry.withNs("http://spring.io/guides/gs-producing-web-service")
                .fromObject(response, GetCountryResponse.class);

        // As SoapBody Object
        // TODO: Enable type matching without specific values like:	.numberType("population")
//		responseForAnExistingCountry = new PactDslSoapBody()
//				.withNs("http://spring.io/guides/gs-producing-web-service")
//				.object("getCountryResponse")
//					.object("country")
//						.stringType("name","Spain")
//						.stringType("capital", "Madrid")
//				.closeObject()
//			.closeObject()
//        .close();

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