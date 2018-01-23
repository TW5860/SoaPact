package au.com.dius.pact.consumer.dsl;

import au.com.dius.pact.soap.converter.JSONConverter;
import au.com.dius.pact.soap.hash.ReadableHash;
import io.gatling.jsonpath.Parser$;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

public class PactDslSoapBody extends PactDslJsonBody {
    private static ReadableHash readableHash = new ReadableHash();

    private Map<String, String> namespaces;
    private Configuration jsonConfig;

    private String mostRecentNameSpace = "test";

    public PactDslSoapBody() {
        jsonConfig = JSONConverter.makeDefaultJSONConfig();
        namespaces = jsonConfig.getXmlToJsonNamespaces();
    }

    public PactDslSoapBody(String s, String s1, DslPart dslPart, Map<String, String> namespaces, String currentNamespace) {
        super(s, s1, dslPart);
        this.namespaces = namespaces;
        this.mostRecentNameSpace = currentNamespace;
    }

    public PactDslSoapBody withNs(String uri) {
        mostRecentNameSpace = readableHash.hashAsReadableString(uri);
        namespaces.put(uri, mostRecentNameSpace);


        JSONObject nsObj = new JSONObject();
        for (Entry<String, String> entry : namespaces.entrySet()) {
            String nsUri = entry.getKey();
            nsObj.put(entry.getValue(), nsUri);
        }
        JSONObject bodyJsonObj = (JSONObject) this.getBody();
        bodyJsonObj.put("#xmlns", nsObj);

        return this;
    }

    /**
     * Creates an exact match on a given Object.
     * Attributes initialized with null will be ignored.
     * Attributes initialized with exact values (integer=0) will be matched to that value exactly.
     *
     * @param obj The object to create a DslBody for
     * @param cls The class type of the object
     * @param <S>
     * @param <T>
     * @return The DslPart containing the object attributes in the body.
     */
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

    @Override
    public PactDslSoapBody numberType(String name) {
        super.numberType(name);
        return this;
    }

    @Override
    public PactDslSoapBody numberType(String name, Number number) {
        super.numberType(mostRecentNameSpace + "#" + name, number);
        return this;
    }

    @Override
    public PactDslSoapBody stringType(String name, String value) {
        super.stringType(mostRecentNameSpace + "#" + name, value);
        return this;
    }

    @Override
    public PactDslSoapBody object() {
        super.object();
        return this;
    }

    @Override
    public PactDslSoapBody object(String name) {
        String base = rootPath + mostRecentNameSpace + "#" + name;
        if (!name.matches(Parser$.MODULE$.FieldRegex().toString())) {
            base = StringUtils.substringBeforeLast(rootPath, ".") + "['" + name + "']";
        }

        return new PactDslSoapBody(base + ".", "", this, namespaces, mostRecentNameSpace);
    }

    public PactDslSoapBody closeObject() {
        if (this.parent != null) {
            this.parent.putObject(this);
        }

        this.closed = true;
        return (PactDslSoapBody) this.parent;
    }

}
