package pact.utils;

import au.com.dius.pact.provider.junit.target.HttpTarget;
import pact.utils.proxy.JSONToSOAPReverseProxy;
import pact.utils.proxy.ReverseProxy;

public class SoapTarget extends HttpTarget {

    ReverseProxy proxy;

    public SoapTarget(int httpPort, int soapPort) {
        super(soapPort);
        this.proxy = new JSONToSOAPReverseProxy("http://localhost:" + httpPort + "/ws/", soapPort);
        proxy.start();
    }

}
