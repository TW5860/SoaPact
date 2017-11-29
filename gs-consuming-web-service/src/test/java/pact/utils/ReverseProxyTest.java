package pact.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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
		String responseText = "Zis is ze responze!";
		OkHttpClient client = new OkHttpClient();

		// Act:
		StaticBackendServer.runTest(responseText, endServer -> {
			ReverseProxy proxy = new ReverseProxy("localhost", 8080, endServer.getUrl());
			proxy.start();
			
			String requestText = "And zis is ze requezt...";
			RequestBody body = RequestBody.create(JSON, requestText);
			Request request = new Request.Builder().url("http://localhost:8080").post(body).build();
			Response response = client.newCall(request).execute();
			
			// Verify:
			assertEquals(requestText, endServer.getLastRequestText());
			assertEquals(responseText, response.body().string());
		});
	}
}
