package au.com.dius.pact.consumer.dsl;

import au.com.dius.pact.soap.converter.JSONConverter;
import au.com.dius.pact.soap.hash.ReadableHash;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PactDslSoapBody2 extends PactDslXmlBody {

    private String mostRecentNameSpace;
    private Map<String,String> namespaces = new HashMap<>();
    private ReadableHash readableHash = new ReadableHash();

    public PactDslSoapBody2() {
        super();
        namespaces = jsonConfig.getXmlToJsonNamespaces();
    }

    public PactDslSoapBody2 withNs(String uri) {
        mostRecentNameSpace = readableHash.hashAsReadableString(uri);
        namespaces.put(uri, mostRecentNameSpace);


        JSONObject nsObj = new JSONObject();
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            String nsUri = entry.getKey();
            nsObj.put(entry.getValue(), nsUri);
        }
        JSONObject bodyJsonObj = (JSONObject) this.getBody();
        bodyJsonObj.put(JSONConverter.JSON_NAMESPACE_SEPARATOR + "xmlns", nsObj);

        return this;
    }
}
