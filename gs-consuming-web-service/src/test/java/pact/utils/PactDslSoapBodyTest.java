package pact.utils;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;

public class PactDslSoapBodyTest {
	//TODO: Remove this!
	//@Test
	public void pseudoTest() {
		PactDslJsonBody body = new PactDslSoapBody()
				.numberValue("a", 3)
				.stringMatcher("b", ".*", "kkqq");
		
		System.out.print("body: ");
		System.out.println(body);

		System.out.print("matchers: ");
		System.out.println(body.getMatchers());

		System.out.print("generators: ");
		System.out.println(body.getGenerators());
	}
	
	@Test
	public void fromObject_setsBodyToAnEquivalentJsonObject() throws Exception {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");
		
		PactDslSoapBody body = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service")
				.fromObject(request, GetCountryRequest.class);

		JSONAssert.assertEquals("{getCountryRequest: {name: \"Spain\"}}", body.toString(), true);
	}
}
