package pact.utils;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import pact.utils.converter.JSONConverter;
import pact.utils.readablehash.ReadableHash;

public class PactDslSoapBody extends PactDslJsonBody {
	private static ReadableHash readableHash = new ReadableHash();

	private Map<String, String> namespaces;
	private Configuration jsonConfig;
	
	public PactDslSoapBody() {
		jsonConfig = JSONConverter.makeDefaultJSONConfig();
		namespaces = jsonConfig.getXmlToJsonNamespaces();
	}
	
	public PactDslSoapBody withNs(String uri) {
		namespaces.put(uri, readableHash.hashAsReadableString(uri));
		return this;
	}

	public <S, T extends S> PactDslSoapBody fromObject(T obj, Class<S> cls) {
		StringWriter writer = new StringWriter();
		try {
			JSONConverter.objToJSON(obj, cls, writer, jsonConfig);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		JSONObject jsonObj = new JSONObject(writer.toString());
		
		JSONObject bodyJsonObj = (JSONObject) this.getBody();
		for (String key : jsonObj.keySet()) {
			Object value = jsonObj.get(key);
			if (value != null) {
				bodyJsonObj.put(key, value);
			} else {
				bodyJsonObj.put(key, JSONObject.NULL);
			}
		}
		
		JSONObject nsObj = new JSONObject();
		for (Entry<String, String> entry : namespaces.entrySet()) {
			String nsUri = entry.getKey();
			nsObj.put(entry.getValue(), nsUri);
		}
		bodyJsonObj.put("#xmlns", nsObj);

		return this;
	}
}
