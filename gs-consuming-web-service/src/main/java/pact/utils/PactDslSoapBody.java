package pact.utils;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import pact.utils.converter.JSONConverter;

public class PactDslSoapBody extends PactDslJsonBody {
	private Map<String, String> namespaces;
	private Configuration jsonConfig;
	
	public PactDslSoapBody() {
		jsonConfig = JSONConverter.makeDefaultJSONConfig();
		namespaces = jsonConfig.getXmlToJsonNamespaces();
	}
	
	public PactDslSoapBody withNs(String uri, String prefix) {
		namespaces.put(uri, prefix);
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

		return this;
	}
}
