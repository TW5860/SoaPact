package au.com.dius.pact.soap.converter;

import au.com.dius.pact.soap.hash.ReadableHash;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SOAPToJSONConverter {
    private static ReadableHash readableHash = new ReadableHash();
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
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
        JSONObject payload = jsonRequest.getJSONObject(withNameSpace("Envelope")).getJSONObject(withNameSpace("Body"));

        JSONObject nsObj = new JSONObject();
        for (Entry<String, String> entry : nsMap.entrySet()) {
            String nsUri = entry.getKey();
            if (!nsUri.equals(SOAP_NS)) {
                nsObj.put(entry.getValue(), nsUri);
            }
        }
        payload.put(withPrefix("xmlns"), nsObj);

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

        JSONObject nsObj = body.getJSONObject(withPrefix("xmlns"));
        body.remove(withPrefix("xmlns"));

        Map<String, String> nsMap = new HashMap<>();
        for (String nsUri : nsObj.keySet()) {
            nsMap.put(nsObj.getString(nsUri), nsUri);
        }
        nsMap.put(SOAP_NS, HASHED_SOAP_NS);

        JSONObject jsonResponse = new JSONObject();

        JSONObject envelope = new JSONObject();
        jsonResponse.put(withNameSpace("Envelope"), envelope);

        JSONObject header = new JSONObject();
        envelope.put(withNameSpace("Header"), header);

        envelope.put(withNameSpace("Body"), body);

        Configuration jsonConfig = makeJSONConfig(nsMap);
        return JSONConverter.jsonToXML(jsonResponse.toString(), jsonConfig);
    }

    @NotNull
    private static String withPrefix(String elementName) {
        return JSONConverter.JSON_NAMESPACE_SEPARATOR + elementName;
    }

    @NotNull
    private static String withNameSpace(String elementName) {
        return HASHED_SOAP_NS + JSONConverter.JSON_NAMESPACE_SEPARATOR + elementName;
    }
}