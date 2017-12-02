package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.mapped.Configuration;

import pact.utils.converter.SOAPToJSONConverter;

public class SOAPToJSON2WayReverseProxy extends ReverseProxy {

	
	
	private Configuration jsonConfig;

	public SOAPToJSON2WayReverseProxy(String backServerURL) {
		super(backServerURL);
		jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");
	}

	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		try {
			String theString = IOUtils.toString(bodyInputStream, "UTF-8");
			return SOAPToJSONConverter.soapRequestToJSON(theString, jsonConfig);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String changeResponse(String bodyText) {
		try {
			return SOAPToJSONConverter.jsonToSoapResponse(bodyText,
					jsonConfig);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new SOAPToJSON2WayReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}
