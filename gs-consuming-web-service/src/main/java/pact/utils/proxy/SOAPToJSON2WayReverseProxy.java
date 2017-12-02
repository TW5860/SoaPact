package pact.utils.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;

import pact.utils.converter.SOAPToJSONConverter;

public class SOAPToJSON2WayReverseProxy extends ReverseProxy {

	public SOAPToJSON2WayReverseProxy(String backServerURL) {
		super(backServerURL);
	}

	@Override
	protected String changeRequest(InputStream bodyInputStream) throws IOException {
		Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://spring.io/guides/gs-producing-web-service", "");

		String soapRequestToJSON;
		try {
			String theString = IOUtils.toString(bodyInputStream, "UTF-8");
			soapRequestToJSON = SOAPToJSONConverter.soapRequestToJSON(theString, jsonConfig);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return soapRequestToJSON;
	}

	@Override
	protected String changeResponse(String bodyText) {
		try {
			Configuration jsonConfig = SOAPToJSONConverter.makeDefaultJSONConfig();
			Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
			namespaces.put("http://spring.io/guides/gs-producing-web-service", "");
			String converted = SOAPToJSONConverter.jsonToSoapResponse(bodyText,
					jsonConfig);
			System.out.println(converted);
			return converted;
		} catch (JSONException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bodyText;
	}

	public static void runTest(String backServerURL, TestCase testCase) {
		ReverseProxy proxy = new SOAPToJSON2WayReverseProxy(backServerURL);
		proxy.runTest(testCase);
	}
}
