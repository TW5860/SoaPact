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
	
	@SuppressWarnings("unchecked")
	public PactDslSoapBody() {
		jsonConfig = JSONConverter.makeDefaultJSONConfig();
		namespaces = jsonConfig.getXmlToJsonNamespaces();
	}

	public PactDslSoapBody withNs(String uri) {
		namespaces.put(uri, "");
		return this;
	}

	public <S, T extends S> PactDslSoapBody fromObject(T obj, Class<S> cls) throws JAXBException {
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
