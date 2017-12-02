package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.apache.cxf.helpers.IOUtils;

import io.undertow.Undertow;
import io.undertow.Undertow.ListenerInfo;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReverseProxy {
	public static MediaType JSON = MediaType.parse("application/json; charset=UTF-8");

	private Undertow server;
	private InetSocketAddress serverAddress;

	public ReverseProxy(String backServerURL) {
		server = Undertow.builder().addHttpListener(0, "localhost").setIoThreads(1)
				.setHandler(reverseProxyHandler(backServerURL)).build();
	}

	private BlockingHandler reverseProxyHandler(String backServerURL) {
		return new BlockingHandler((HttpServerExchange exchange) -> {
			// RECEIVE
			InputStream inputStream = exchange.getInputStream();
			// CHANGE REQUEST
			String changedRequest = changeRequest(inputStream);
			// SEND REQUEST & RECEIVE RESPONSE
			Response response = sendRequest(backServerURL, changedRequest);
			// CHANGE RESPONSE
			String changedResponse = changeResponse(response.body().string());
			// SENDBACK
			exchange.getResponseSender().send(changedResponse);
		});
	}

	protected String changeResponse(String response) {
		// This simple reverse proxy does not alter the response
		return response;
	}

	protected String changeRequest(InputStream inputStream) throws IOException {
		// This simple reverse proxy does not alter the response
		// It just converts the stream to a string
		return IOUtils.toString(inputStream);
	}

	private Response sendRequest(String backServerURL, String bodyText) throws IOException {
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(JSON, bodyText);
		Request request = new Request.Builder().url(backServerURL).post(body).build();
		return client.newCall(request).execute();
	}

	public void start() {
		server.start();
		
		ListenerInfo listenerInfo = server.getListenerInfo().get(0);
		serverAddress = (InetSocketAddress) listenerInfo.getAddress();
	}
	
	public void stop() {
		server.stop();
	}
	
	public String getUrl() {
		return "http://" + serverAddress.getHostName() + ":" + serverAddress.getPort() + "/";
	}


	public static interface TestCase {
		void run(ReverseProxy proxy) throws Exception;
	}

	protected void runTest(TestCase testCase) {
		start();
		try {
			testCase.run(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			stop();
		}
	}
	
	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new ReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}