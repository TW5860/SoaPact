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
    public static final String NAMESPACE_PREFIX = "#";

    private static ReadableHash readableHash = new ReadableHash();

    private Map<String, String> namespaces;
    private Configuration jsonConfig;

    private String mostRecentNameSpace = "";

    public PactDslSoapBody() {
        jsonConfig = JSONConverter.makeDefaultJSONConfig();
        namespaces = jsonConfig.getXmlToJsonNamespaces();
    }

    public PactDslSoapBody(String rootPath, String rootName, DslPart parent, Map<String, String> namespaces, String currentNamespace) {
        super(rootPath, rootName, parent);
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
        bodyJsonObj.put(NAMESPACE_PREFIX + "xmlns", nsObj);

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

    //region PactDslJsonBody overrides to add the namespace

    //TODO: Experiment with PactDslJsonBody and the rootname field

    @Override
    public PactDslSoapBody stringValue(String name, String value) {
        super.stringValue(mostRecentNameSpace + NAMESPACE_PREFIX + name, value);
        return this;
    }

    @Override
    public PactDslSoapBody numberValue(String name, Number value) {
        super.numberValue(mostRecentNameSpace + NAMESPACE_PREFIX + name, value);
        return this;
    }

    @Override
    public PactDslSoapBody booleanValue(String name, Boolean value) {
        super.booleanValue(mostRecentNameSpace + NAMESPACE_PREFIX + name, value);
        return this;
    }

    @Override
    public PactDslSoapBody numberType(String name) {
        super.numberType(name);
        return this;
    }

    @Override
    public PactDslSoapBody numberType(String... names) {
        for (String name : names) {
            this.numberType(name);
        }
        return this;
    }

    @Override
    public PactDslSoapBody numberType(String name, Number number) {
        super.numberType(mostRecentNameSpace + NAMESPACE_PREFIX + name, number);
        return this;
    }

    @Override
    public PactDslSoapBody stringType(String name) {
        super.stringType(name);
        return this;
    }

    @Override
    public PactDslSoapBody stringType(String... names) {
        for (String name : names) {
            super.stringType(name);
        }
        return this;
    }

    @Override
    public PactDslSoapBody stringType(String name, String example) {
        super.stringType(mostRecentNameSpace + NAMESPACE_PREFIX + name, example);
        return this;
    }

    @Override
    public PactDslSoapBody integerType(String name) {
        super.integerType(name);
        return this;
    }

    @Override
    public PactDslSoapBody integerType(String... names) {
        for (String name : names) {
            this.integerType(name);
        }
        return this;
    }

    @Override
    public PactDslSoapBody integerType(String name, Long number) {
        super.integerType(mostRecentNameSpace + NAMESPACE_PREFIX + name, number);
        return this;
    }

    @Override
    public PactDslSoapBody integerType(String name, Integer number) {
        super.integerType(mostRecentNameSpace + NAMESPACE_PREFIX + name, number);
        return this;
    }

    @Override
    public PactDslSoapBody decimalType(String name) {
        super.decimalType(name);
        return this;
    }

    @Override
    public PactDslSoapBody decimalType(String name, Double value) {
        super.decimalType(mostRecentNameSpace + NAMESPACE_PREFIX + name, value);
        return this;
    }

    @Override
    public PactDslSoapBody booleanType(String name) {
        super.booleanType(name);
        return this;
    }

    @Override
    public PactDslSoapBody booleanType(String... names) {
        for (String name : names) {
            super.booleanType(name);
        }
        return this;
    }

    @Override
    public PactDslSoapBody booleanType(String name, Boolean example) {
        super.booleanType(name, example);
        return this;
    }

    @Override
    public PactDslJsonBody stringMatcher(String name, String regex, String example) {
        super.stringMatcher(mostRecentNameSpace + NAMESPACE_PREFIX + name, regex, example);
        return this;
    }

    @Override
    public PactDslJsonBody timestamp() {
        super.timestamp();
        return this;
    }

    @Override
    public PactDslJsonBody timestamp(String name) {
        super.timestamp(mostRecentNameSpace + NAMESPACE_PREFIX + name);
        return this;
    }

    @Override
    public PactDslJsonBody timestamp(String name, String format) {
        super.timestamp(mostRecentNameSpace + NAMESPACE_PREFIX + name, format);
        return this;
    }


    @Override
    public PactDslSoapBody object() {
        super.object();
        return this;
    }

    @Override
    public PactDslSoapBody object(String name) {
        String base = rootPath + mostRecentNameSpace + NAMESPACE_PREFIX + name;
        if (!name.matches(Parser$.MODULE$.FieldRegex().toString())) {
            base = StringUtils.substringBeforeLast(rootPath, ".") + "['" + name + "']";
        }

        return new PactDslSoapBody(base + ".", "", this, namespaces, mostRecentNameSpace);
    }

    @Override
    public PactDslSoapBody closeObject() {
        return (PactDslSoapBody) super.closeObject();
    }

    @Override
    public PactDslSoapBody closeArray() {
        return (PactDslSoapBody) super.closeObject();
    }

    @Override
    public PactDslSoapBody eachLike(String name) {
        super.eachLike(name);
        return this;
    }

    @Override
    public PactDslSoapBody eachLike(String name, int numExamples) {
        super.eachLike(mostRecentNameSpace + NAMESPACE_PREFIX + name, numExamples);
        return this;
    }

    @Override
    public PactDslSoapBody minArrayLike(String name, Integer size) {
        super.minArrayLike(name, size);
        return this;
    }

    @Override
    public PactDslSoapBody minArrayLike(String name, Integer size, int numExamples) {
        super.minArrayLike(mostRecentNameSpace + NAMESPACE_PREFIX + name, size, numExamples);
        return this;
    }

    @Override
    public PactDslSoapBody maxArrayLike(String name, Integer size) {
        super.maxArrayLike(name, size);
        return this;
    }

    @Override
    public PactDslSoapBody maxArrayLike(String name, Integer size, int numExamples) {
        super.maxArrayLike(mostRecentNameSpace + NAMESPACE_PREFIX + name, size, numExamples);
        return this;
    }

    //endregion

}
