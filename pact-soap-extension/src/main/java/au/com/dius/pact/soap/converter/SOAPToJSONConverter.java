package au.com.dius.pact.soap.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import au.com.dius.pact.soap.hash.ReadableHash;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

public class SOAPToJSONConverter {
	private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";

	private static ReadableHash readableHash = new ReadableHash();
	private static final String HASHED_SOAP_NS = readableHash.hashAsReadableString(SOAP_NS);

	private static Configuration makeJSONConfig(Map<String, String> namespaces) {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> configNamespaces = jsonConfig.getXmlToJsonNamespaces();
		configNamespaces.putAll(namespaces);
		return jsonConfig;
	}

	public static String soapRequestToJSON(String soapRequestXML) throws XMLStreamException, JSONException {
		Map<String, String> nsMap = namespaceMapForXML(soapRequestXML);
		Configuration jsonConfig = makeJSONConfig(nsMap);
		
		String jsonRequestText = JSONConverter.xmlToJSON(soapRequestXML, jsonConfig);
		JSONObject jsonRequest = new JSONObject(jsonRequestText);
		JSONObject payload = jsonRequest.getJSONObject(HASHED_SOAP_NS + "___Envelope").getJSONObject(HASHED_SOAP_NS + "___Body");

		JSONObject nsObj = new JSONObject();
		for (Entry<String, String> entry : nsMap.entrySet()) {
			String nsUri = entry.getKey();
			if (!nsUri.equals(SOAP_NS)) {
				nsObj.put(entry.getValue(), nsUri);
			}
		}
		payload.put("___xmlns", nsObj);

		return payload.toString();
	}
	
	public static Map<String, String> namespaceMapForXML(String xml) {
		Map<String, String> nsMap = new HashMap<>();
		for (String nsUri : XMLNamespaceExtractor.namespacesFromXML(xml)) {
			nsMap.put(nsUri, readableHash.hashAsReadableString(nsUri));
		}
		
		return nsMap;
	}

	public static String jsonToSoapResponse(String jsonResponsePayloadText) throws JSONException, XMLStreamException {	
		JSONObject body = new JSONObject(jsonResponsePayloadText);

		JSONObject nsObj = body.getJSONObject("___xmlns");
		body.remove("___xmlns");

		Map<String, String> nsMap = new HashMap<>();
		for (String nsUri : nsObj.keySet()) {
			nsMap.put(nsObj.getString(nsUri), nsUri);
		}
		nsMap.put(SOAP_NS, HASHED_SOAP_NS);
		
		JSONObject jsonResponse = new JSONObject();
		
		JSONObject envelope = new JSONObject();
		jsonResponse.put(HASHED_SOAP_NS + "___Envelope", envelope);
		
		JSONObject header = new JSONObject();
		envelope.put(HASHED_SOAP_NS + "___Header", header);
		
		envelope.put(HASHED_SOAP_NS + "___Body", body);
		
		Configuration jsonConfig = makeJSONConfig(nsMap);
		return JSONConverter.jsonToXML(jsonResponse.toString(), jsonConfig);
	}
}