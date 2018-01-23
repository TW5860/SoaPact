package au.com.dius.pact.soap.proxy;

import au.com.dius.pact.soap.converter.SOAPToJSONConverter;
import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.json.JSONException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

public class JSONToSOAPReverseProxy extends ReverseProxy {

    public JSONToSOAPReverseProxy(String backServerURL) {
        this(backServerURL,0);
    }

    public JSONToSOAPReverseProxy(String backServerURL, int soapPort) {
        super(backServerURL, soapPort);
        this.requestContentType = XML_MEDIA_TYPE;
        this.responseContentType = JSON_CONTENT;
    }

    public static void runTest(String backServerURL, TestCase testCase) {
        ReverseProxy proxy = new JSONToSOAPReverseProxy(backServerURL);
        proxy.runTest(testCase);
    }

    @Override
    protected String changeRequest(InputStream bodyInputStream) throws IOException {
        String bodyText = IOUtils.toString(bodyInputStream);
        System.out.println("Request was :" + bodyText);
        try {
            String json = SOAPToJSONConverter.jsonToSoapResponse(bodyText);
            json = json.replace("<?xml version='1.0'?>", "");
            System.out.println("Sending request :" + json);
            return json;
        } catch (XMLStreamException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String changeResponse(String bodyText) {
        try {
            System.out.println("Response was :" + bodyText);
            String xml = SOAPToJSONConverter.soapRequestToJSON(bodyText);
            System.out.println("Sending response :" + xml);
            return xml;
        } catch (JSONException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
