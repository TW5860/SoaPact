package au.com.dius.pact.soap.proxy;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import au.com.dius.pact.soap.converter.SOAPToJSONConverter;
import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.json.JSONException;

public class SOAPToJSONReverseProxy extends ReverseProxy {

	public SOAPToJSONReverseProxy(String backServerURL) {
		this(backServerURL,0);
	}

	public SOAPToJSONReverseProxy(String backServerURL, int port) {
		super(backServerURL,port);
		this.requestContentType = JSON_MEDIA_TYPE;
		this.responseContentType = XML_CONTENT;
	}

	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		String bodyText = IOUtils.toString(bodyInputStream);
			try {
				return SOAPToJSONConverter.soapRequestToJSON(bodyText);
			} catch (XMLStreamException | JSONException e) {
				throw new RuntimeException(e);
			}
	}

	@Override
	protected String changeResponse(String bodyText) {
			try {
				return SOAPToJSONConverter.jsonToSoapResponse(bodyText);
			} catch (JSONException | XMLStreamException e) {
				throw new RuntimeException(e);
			}
	}

	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new SOAPToJSONReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}
