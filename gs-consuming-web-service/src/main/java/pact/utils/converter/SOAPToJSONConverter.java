package pact.utils.converter;

import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

public class SOAPToJSONConverter {
	public static Configuration makeDefaultJSONConfig() {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> namespaces = jsonConfig.getXmlToJsonNamespaces();
		namespaces.put("http://schemas.xmlsoap.org/soap/envelope/", "soap");
		return jsonConfig;
	}

	public static String soapRequestToJSON(String soapRequestXML, Configuration jsonConfig) throws XMLStreamException, JSONException {
		String jsonRequestText = JSONConverter.xmlToJSON(soapRequestXML, jsonConfig);
		JSONObject jsonRequest = new JSONObject(jsonRequestText);
		JSONObject payload = jsonRequest.getJSONObject("soap#Envelope").getJSONObject("soap#Body");
		return payload.toString();
	}

	public static String jsonToSoapResponse(String jsonResponsePayloadText, Configuration jsonConfig) throws JSONException, XMLStreamException {
		JSONObject jsonResponse = new JSONObject();

		JSONObject envelope = new JSONObject();
		jsonResponse.put("soap#Envelope", envelope);
		
		JSONObject header = new JSONObject();
		envelope.put("soap#Header", header);

		JSONObject body = new JSONObject(jsonResponsePayloadText);
		envelope.put("soap#Body", body);

		return JSONConverter.jsonToXML(jsonResponse.toString(), jsonConfig);
	}
}
