package country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReverseProxy {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static void main(String[] args) throws IOException, URISyntaxException {		
		Undertow backServer = Undertow.builder()
                .addHttpListener(8089, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                    	System.out.println("In back server: " + exchange.getQueryString());
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Hello World");
                    }
                })
                .build();
		backServer.start();

		Undertow server = Undertow.builder()
                .addHttpListener(8088, "localhost")
                .setIoThreads(1)
                .setHandler(new BlockingHandler((HttpServerExchange exchange) -> {
					InputStream inputStream = exchange.getInputStream();
					String bodyText = new BufferedReader(new InputStreamReader(inputStream))
							  .lines()
							  .collect(Collectors.joining("\n"));
					System.out.println("POST: " + bodyText);

					OkHttpClient client = new OkHttpClient();

					RequestBody body = RequestBody.create(JSON, bodyText);
					Request request = new Request.Builder()
							.url("http://localhost:8089/")
							.post(body)
							.build();
					Response response = client.newCall(request).execute();
					System.out.println("front: " + response.body().string());
		
					exchange.getResponseSender().send("Yeah!");
				}))
                .build();

		server.start();
	}

}
