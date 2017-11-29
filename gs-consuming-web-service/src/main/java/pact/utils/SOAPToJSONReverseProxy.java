package pact.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import pact.utils.ReverseProxy.TestCase;

public class SOAPToJSONReverseProxy extends ReverseProxy {
	
	public SOAPToJSONReverseProxy(String backServerURL) {
		super(backServerURL);
	}
	
	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		Reader reader = new InputStreamReader(bodyInputStream);
		StringWriter writer = new StringWriter();

		try {
			JSONConverter.xmlToJSON(reader, writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return writer.toString();
	}

	@Override
	protected String changeResponse(String bodyText) {
		StringWriter writer = new StringWriter();

		try {
			JSONConverter.jsonToXML(bodyText, writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return writer.toString();
	}

	
	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new SOAPToJSONReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}
