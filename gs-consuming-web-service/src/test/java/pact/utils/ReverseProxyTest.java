package pact.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import country.CountryConfiguration;
import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReverseProxyTest {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Test
	public void shouldPassAnXMLResponseUnmodified() throws IOException {
		// Prepare:

		// End server.
		String staticResponse = "Zis is ze responze!";
		StaticBackendServer endServer = new StaticBackendServer("localhost", 9999, staticResponse);
		endServer.start();

		// HTTP client.
		OkHttpClient client = new OkHttpClient();

		// Act:
		ReverseProxy proxy = new ReverseProxy("localhost", 8080, "http://localhost:9999");
		proxy.start();
		
		RequestBody body = RequestBody.create(JSON, "");
		Request request = new Request.Builder().url("http://localhost:8080").post(body).build();
		Response response = client.newCall(request).execute();
		
		// Verify:
		assertEquals(staticResponse, response.body().string());
	}
}
