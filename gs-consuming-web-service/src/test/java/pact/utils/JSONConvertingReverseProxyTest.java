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
	public void shouldConvertBetweenXMLandJSON() throws IOException {
		// Prepare:
		String responseText = "{x: {y: 1}}";
		OkHttpClient client = new OkHttpClient();

		// Act:
		StaticBackendServer.runTest(responseText, endServer -> {
			SOAPToJSONReverseProxy.runTest(endServer.getUrl(), proxy -> {			
				String requestText = "<a><b>xxx</b><c>yyy</c></a>";
				RequestBody body = RequestBody.create(JSON, requestText);
				Request request = new Request.Builder().url(proxy.getUrl()).post(body).build();
				Response response = client.newCall(request).execute();
				
				// Verify:
				JSONAssert.assertEquals("{a: {b: \"xxx\", c: \"yyy\"}}",
						endServer.getLastRequestText(), true);
				assertThat(response.body().string(),
						XMLCompare.isEquivalentXMLTo("<x><y>1</y></x>"));
			});
		});
	}
}
