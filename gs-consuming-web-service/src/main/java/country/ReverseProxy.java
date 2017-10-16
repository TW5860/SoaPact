package country;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReverseProxy {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private int listenerPort;
	private String hostName;
	private String backServerURL;

	@SuppressWarnings("unused")
	private ReverseProxy() {};
	
	public ReverseProxy(String hostName, int port, String backServerURL) {
		this.hostName = hostName;
		listenerPort = port;
		this.backServerURL = backServerURL;
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
		Undertow server = Undertow.builder().addHttpListener(listenerPort, hostName).setIoThreads(1)
				.setHandler(reverseProxyHandler(backServerURL)).build();
		server.start();
	}

	// TODO: CONVERT EXAMPLES TO TEST CASES
	// EXAMPLE
	//
//	 public static void main(String args[]) {
//	 startBackServerWithStaticResponse("localhost", 8081, "{ast:\"asd\"}");
//	 new ReverseProxy("localhost", 8080, "http://localhost:8081").start();
//	 }

	// EXAMPLE BACKSERVER
	//
//	protected static void startBackServerWithStaticResponse(String hostname, int port, String response) {
//		Undertow backServer = Undertow.builder().addHttpListener(port, hostname)
//				.setHandler(new BlockingHandler((HttpServerExchange exchange) -> {
//					InputStream inputStream = exchange.getInputStream();
//					String bodyText = new BufferedReader(new InputStreamReader(inputStream)).lines()
//							.collect(Collectors.joining("\n"));
//					System.out.println("Received Request Backserver: " + bodyText);
//					exchange.getResponseSender().send(response);
//				})).build();
//		backServer.start();
//	}
}
