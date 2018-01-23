package au.com.dius.pact.consumer.dsl;

import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class PactDslSoapBodyTest {

	@Test
	public void fromObject_setsBodyToAnEquivalentJsonObject() throws Exception {
		GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");

		PactDslSoapBody body = new PactDslSoapBody()
				.withNs("http://spring.io/guides/gs-producing-web-service")
				.fromObject(request, GetCountryRequest.class);

		JSONAssert.assertEquals("{\"#xmlns\": {\"Je\": \"http://spring.io/guides/gs-producing-web-service\"},"
				+ "\"Je#getCountryRequest\": {\"Je#name\": \"Spain\"}}",
				body.toString(), true);
	}
}
