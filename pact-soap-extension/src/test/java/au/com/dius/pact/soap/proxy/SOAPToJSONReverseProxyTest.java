package au.com.dius.pact.soap.proxy;

import au.com.dius.pact.consumer.dsl.FileReader;
import au.com.dius.pact.consumer.dsl.StaticBackendServer;
import okhttp3.*;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import au.com.dius.pact.consumer.dsl.XMLCompare;

import java.io.IOException;

import static org.junit.Assert.assertThat;

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

    @Test
    public void shouldConvertBetweenJSONandXML() throws IOException {
        // Prepare:
        String responseText = FileReader.readFile("ValidSoapResponse.xml");
        OkHttpClient client = new OkHttpClient();

        // Act:
        StaticBackendServer.runTest(responseText, endServer -> {
            JSONToSOAPReverseProxy.runTest(endServer.getUrl(), proxy -> {
                // Act:
                String requestText = FileReader.readFile("ValidSoapRequestInJSON.json");
                RequestBody body = RequestBody.create(JSON, requestText);
                Request request = new Request.Builder().url(proxy.getUrl()).post(body).build();
                Response response = client.newCall(request).execute();

                // Verify:
                String expectedXMLRequest = FileReader.readFile("ValidSoapRequest.xml");
//                JSONAssert.assertEquals(expectedXMLRequest,
//                        endServer.getLastRequestText(), true);
                assertThat(expectedXMLRequest,
                        XMLCompare.isEquivalentXMLTo(endServer.getLastRequestText()));
                String expectedJSONResponse = FileReader.readFile("ValidSoapResponseInJSON.json");
                JSONAssert.assertEquals(response.body().string(), expectedJSONResponse, true);
            });
        });
    }
}
