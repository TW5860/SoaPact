package pact.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import io.undertow.Undertow;
import io.undertow.Undertow.ListenerInfo;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;

public class StaticBackendServer {
	private Undertow backServer;
	private InetSocketAddress serverAddress;

	private String lastRequestText;

	public StaticBackendServer(String responseText) {
		backServer = Undertow.builder().addHttpListener(0, "localhost")
				.setHandler(new BlockingHandler((HttpServerExchange exchange) -> {
					InputStream inputStream = exchange.getInputStream();
					String bodyText = new BufferedReader(new InputStreamReader(inputStream)).lines()
							.collect(Collectors.joining("\n"));
					lastRequestText = bodyText;
					exchange.getResponseSender().send(responseText);
				})).build();
	}
		
	private void start() {
		backServer.start();
		
		ListenerInfo listenerInfo = backServer.getListenerInfo().get(0);
		serverAddress = (InetSocketAddress) listenerInfo.getAddress();
	}
	
	private void stop() {
		backServer.stop();
	}
	
	public String getUrl() {
		return "http://" + serverAddress.getHostName() + ":" + serverAddress.getPort() + "/";
	}
	
	public String getLastRequestText() {
		return lastRequestText;
	}
	
	
	public static interface TestCase {
		void run(StaticBackendServer staticServer) throws Exception;
	}

	public static void runTest(String responseText,
			TestCase testCase) {
		StaticBackendServer staticServer = new StaticBackendServer(responseText);
		staticServer.start();
		try {
			testCase.run(staticServer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			staticServer.stop();
		}
	}
}
