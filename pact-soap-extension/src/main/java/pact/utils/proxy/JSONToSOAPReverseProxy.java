package pact.utils.proxy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.json.JSONException;
import pact.utils.converter.SOAPToJSONConverter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

public class JSONToSOAPReverseProxy extends ReverseProxy {

    public JSONToSOAPReverseProxy(String backServerURL) {
        super(backServerURL);
        this.mediaType = XMLType;
    }

    public JSONToSOAPReverseProxy(String backServerURL, int soapPort) {
        super(backServerURL, soapPort);
        this.mediaType = XMLType;
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
