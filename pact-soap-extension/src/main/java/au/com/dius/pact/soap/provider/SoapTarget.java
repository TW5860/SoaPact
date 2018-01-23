package au.com.dius.pact.soap.provider;

import au.com.dius.pact.soap.proxy.JSONToSOAPReverseProxy;
import au.com.dius.pact.soap.proxy.ReverseProxy;
import au.com.dius.pact.provider.junit.target.HttpTarget;

public class SoapTarget extends HttpTarget {

    ReverseProxy proxy;

    public SoapTarget(int httpPort, int soapPort) {
        super(soapPort);
        this.proxy = new JSONToSOAPReverseProxy("http://localhost:" + httpPort + "/ws/", soapPort);
        proxy.start();
    }

}
