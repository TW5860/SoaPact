package pact.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;

public class StaticBackendServer {
	
	private String hostname;
	private int port;
	private String responseText;
	private String lastRequestText;

	public StaticBackendServer(String hostname, int port, String response) {
		this.hostname = hostname;
		this.port = port;
		this.responseText = response;
	}
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void start() {
		Undertow backServer = Undertow.builder().addHttpListener(port, hostname)
				.setHandler(new BlockingHandler((HttpServerExchange exchange) -> {
					InputStream inputStream = exchange.getInputStream();
					String bodyText = new BufferedReader(new InputStreamReader(inputStream)).lines()
							.collect(Collectors.joining("\n"));
					lastRequestText = bodyText;
					exchange.getResponseSender().send(responseText);
				})).build();
		backServer.start();
	}
	
	public String getLastRequestText() {
		return lastRequestText;
	}
}