package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.mapped.Configuration;

import okhttp3.MediaType;
import pact.utils.converter.SOAPToJSONConverter;

public class JSONToSOAP2WayReverseProxy extends ReverseProxy {

	public JSONToSOAP2WayReverseProxy(String backServerURL) {
		super(backServerURL);
	}

	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");
		JSON = MediaType.parse("text/xml; charset=UTF-8");		
		String soapRequestToJSON;
		try {
			String theString = IOUtils.toString(bodyInputStream, "UTF-8");
			System.err.println("Request not conv:" + theString);
			soapRequestToJSON = SOAPToJSONConverter.jsonToSoapResponse(theString, jsonConfig);
			soapRequestToJSON = soapRequestToJSON.replace("<?xml version='1.0'?>", "");
			System.err.println("Request conv:" + soapRequestToJSON);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return soapRequestToJSON;
	}

	@Override
	protected String changeResponse(String bodyText) {
		JSON = MediaType.parse("application/json; charset=UTF-8");
		try {
			System.err.println("Response non converted:" + bodyText);
			Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
			Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
			namespaces.put("http://spring.io/guides/gs-producing-web-service", "");
			String converted = SOAPToJSONConverter.soapRequestToJSON(bodyText, jsonConfig);
			System.err.println("Response converted:" + converted);
			return converted;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bodyText;
	}

	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new JSONToSOAP2WayReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}
