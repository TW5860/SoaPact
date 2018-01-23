package au.com.dius.pact.soap.proxy;

import io.undertow.Undertow;
import io.undertow.Undertow.ListenerInfo;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.HttpString;
import okhttp3.*;
import org.apache.cxf.helpers.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class ReverseProxy {

    protected final static String JSON_CONTENT = "application/json; charset=UTF-8";
    protected final static String XML_CONTENT = "text/xml; charset=UTF-8";

    protected static MediaType JSON_MEDIA_TYPE = MediaType.parse(JSON_CONTENT);
    protected static MediaType XML_MEDIA_TYPE = MediaType.parse(XML_CONTENT);

    protected MediaType requestContentType = JSON_MEDIA_TYPE;
    protected String responseContentType = XML_CONTENT;

    private Undertow server;
    private InetSocketAddress serverAddress;

    public ReverseProxy(String backServerURL) {
        this(backServerURL,0);
    }

    public ReverseProxy(String backServerURL, int proxyPort) {
        server = Undertow.builder().addHttpListener(proxyPort, "localhost").setIoThreads(1)
                .setHandler(reverseProxyHandler(backServerURL)).build();
    }

    public static void runTest(String backServerURL, TestCase testCase) {
        ReverseProxy proxy = new ReverseProxy(backServerURL);
        proxy.runTest(testCase);
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
            sendResponse(exchange, changedResponse);
        });
    }

    private void sendResponse(HttpServerExchange exchange, String changedResponse) {
        exchange.getResponseHeaders().add(new HttpString("Content-Type"),responseContentType);
        exchange.getResponseSender().send(changedResponse);
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
        RequestBody body = RequestBody.create(requestContentType, bodyText);
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

    public int getPort() {
        return serverAddress.getPort();
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

    public static interface TestCase {
        void run(ReverseProxy proxy) throws Exception;
    }
}