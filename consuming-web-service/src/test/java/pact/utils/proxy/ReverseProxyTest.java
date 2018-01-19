package pact.utils.proxy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pact.utils.StaticBackendServer;

public class ReverseProxyTest {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Test
	public void shouldPassAnXMLResponseUnmodified() throws IOException {
		// Prepare:
		String responseText = "Zis is ze responze!";
		OkHttpClient client = new OkHttpClient();

		// Act:
		StaticBackendServer.runTest(responseText, endServer -> {
			ReverseProxy.runTest(endServer.getUrl(), proxy -> {
				String requestText = "And zis is ze requezt...";
				RequestBody body = RequestBody.create(JSON, requestText);
				Request request = new Request.Builder().url(proxy.getUrl())
						.post(body).build();
				Response response = client.newCall(request).execute();
				
				// Verify:
				assertEquals(requestText, endServer.getLastRequestText());
				assertEquals(responseText, response.body().string());
			});
		});
	}
}
