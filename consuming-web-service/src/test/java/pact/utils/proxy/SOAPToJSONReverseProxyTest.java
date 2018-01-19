package pact.utils.proxy;

import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pact.utils.FileReader;
import pact.utils.StaticBackendServer;
import pact.utils.XMLCompare;

public class SOAPToJSONReverseProxyTest {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Test
	public void shouldConvertBetweenXMLandJSON() throws IOException {
		// Prepare:
		String responseText = FileReader.readFile("ValidSoapResponseInJSON.json");
		OkHttpClient client = new OkHttpClient();

		// Act:
		StaticBackendServer.runTest(responseText, endServer -> {
			SOAPToJSONReverseProxy.runTest(endServer.getUrl(), proxy -> {	
				// Act:
				String requestText = FileReader.readFile("ValidSoapRequest.xml");
				RequestBody body = RequestBody.create(JSON, requestText);
				Request request = new Request.Builder().url(proxy.getUrl()).post(body).build();
				Response response = client.newCall(request).execute();
				
				// Verify:
				String expectedJSONRequest = FileReader.readFile("ValidSoapRequestInJSON.json");
				JSONAssert.assertEquals(expectedJSONRequest,
						endServer.getLastRequestText(), true);
				String expectedXMLResponse = FileReader.readFile("ValidSoapResponse.xml");
				assertThat(response.body().string(),
						XMLCompare.isEquivalentXMLTo(expectedXMLResponse));
			});
		});
	}
}
