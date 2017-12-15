package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.mapped.Configuration;

import pact.utils.converter.JSONConverter;
import pact.utils.converter.SOAPToJSONConverter;

public class SOAPToJSONReverseProxy extends ReverseProxy {

	public SOAPToJSONReverseProxy(String backServerURL) {
		super(backServerURL);
	}

	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		String bodyText = IOUtils.toString(bodyInputStream);
		try {
			return SOAPToJSONConverter.soapRequestToJSON(bodyText);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String changeResponse(String bodyText) {
		try {
			return SOAPToJSONConverter.jsonToSoapResponse(bodyText);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new SOAPToJSONReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}
