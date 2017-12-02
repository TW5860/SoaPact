package pact.utils.proxy;

public class ProxyApplication {
	public static void main(String[] args) {
		ReverseProxy proxy = new JSONToSOAP2WayReverseProxy("http://localhost:8080/ws/");
		proxy.start();
		System.out.println(proxy.getUrl());
	}
}
