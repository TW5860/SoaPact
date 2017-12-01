package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.mapped.Configuration;

import pact.utils.converter.SOAPToJSONConverter;

public class SOAPToJSONReverseProxy extends ReverseProxy {
	
	private Configuration jsonConfig;


	public SOAPToJSONReverseProxy(String backServerURL, Configuration jsonConfig) {
		super(backServerURL);
		
		this.jsonConfig = jsonConfig;
	}
	
	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		String bodyText = IOUtils.toString(bodyInputStream);

		try {
			return SOAPToJSONConverter.soapRequestToJSON(bodyText, jsonConfig);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String changeResponse(String bodyText) {
		try {
			return SOAPToJSONConverter.jsonToSoapResponse(bodyText, jsonConfig);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	public static void runTest(String backServerURL, Configuration jsonConfig, TestCase testCase) {
		ReverseProxy proxy = new SOAPToJSONReverseProxy(backServerURL, jsonConfig);
		proxy.runTest(testCase);
	}
}
