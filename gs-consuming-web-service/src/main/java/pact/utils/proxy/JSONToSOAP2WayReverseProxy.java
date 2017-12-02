package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.mapped.Configuration;

import okhttp3.MediaType;
import pact.utils.converter.SOAPToJSONConverter;

public class JSONToSOAP2WayReverseProxy extends ReverseProxy {

	private Configuration jsonConfig;

	public JSONToSOAP2WayReverseProxy(String backServerURL, Configuration jsonConfig) {
		super(backServerURL);
		this.jsonConfig = jsonConfig;
	}

	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		JSON = MediaType.parse("text/xml; charset=UTF-8");
		String bodyText = IOUtils.toString(bodyInputStream, "UTF-8");
		try {
			return SOAPToJSONConverter.jsonToSoapResponse(bodyText, jsonConfig);
			// soapRequestToJSON = soapRequestToJSON.replace("<?xml version='1.0'?>", "");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String changeResponse(String bodyText) {
		JSON = MediaType.parse("application/json; charset=UTF-8");
		try {
			return SOAPToJSONConverter.soapRequestToJSON(bodyText, jsonConfig);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new JSONToSOAP2WayReverseProxy(backServerURL, SOAPToJSONConverter.makeDefaultJSONConfig());
		proxy.runTest(testCase);
	}

	public static void runTest(String backServerURL, Configuration jsonConfig, TestCase testCase) {
		ReverseProxy proxy = new JSONToSOAP2WayReverseProxy(backServerURL, jsonConfig);
		proxy.runTest(testCase);
	}
}
