package pact.utils.proxy;

import java.util.Map;

import org.codehaus.jettison.mapped.Configuration;

import pact.utils.converter.SOAPToJSONConverter;

public class ProxyApplication {
	public static void main(String[] args) {
		Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");

		ReverseProxy proxy = new JSONToSOAP2WayReverseProxy("http://localhost:8080/ws/", jsonConfig);
		proxy.start();
		System.out.println(proxy.getUrl());
	}
}
