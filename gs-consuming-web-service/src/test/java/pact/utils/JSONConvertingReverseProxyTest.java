package pact.utils;

import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JSONConvertingReverseProxyTest {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Test
	public void should() throws IOException {
		// Prepare:

		// End server.
		String responseText = "{x: {y: 1}}";
		StaticBackendServer endServer = new StaticBackendServer("localhost", 9999, responseText);
		endServer.start();

		// HTTP client.
		OkHttpClient client = new OkHttpClient();

		// Act:
		ReverseProxy proxy = new JSONConvertingReverseProxy("localhost", 8080, "http://localhost:9999");
		proxy.start();
		
		String requestText = "<a><b>xxx</b><c>yyy</c></a>";
		RequestBody body = RequestBody.create(JSON, requestText);
		Request request = new Request.Builder().url("http://localhost:8080").post(body).build();
		Response response = client.newCall(request).execute();
		
		// Verify:
		JSONAssert.assertEquals("{a: {b: \"xxx\", c: \"yyy\"}}", endServer.getLastRequestText(), true);
		assertThat(response.body().string(), XMLCompare.isEquivalentXMLTo("<x><y>1</y></x>"));
	}
}
