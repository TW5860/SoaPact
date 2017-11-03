package pact.utils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

public class PactDslSoapBody extends PactDslJsonBody {
	private Map<String, String> namespaces;
	
	public PactDslSoapBody() {
		namespaces = new HashMap<String, String>();
	}

	public PactDslSoapBody withNs(String uri) {
		namespaces.put(uri, "");
		return this;
	}

	public <S, T extends S> PactDslSoapBody fromObject(T obj, Class<S> cls) throws JAXBException {
		Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
		Map<String, String> configNamespaces = jsonConfig.getXmlToJsonNamespaces();
		configNamespaces.putAll(namespaces);
		
		StringWriter writer = new StringWriter();
		JSONConverter.objToJSON(obj, cls, writer, jsonConfig);
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

		return this;
	}
}
