package au.com.dius.pact.consumer.dsl;

import au.com.dius.pact.model.generators.Category;
import au.com.dius.pact.model.generators.RandomStringGenerator;
import au.com.dius.pact.model.matchingrules.TypeMatcher;
import au.com.dius.pact.soap.converter.JSONConverter;
import org.codehaus.jettison.mapped.Configuration;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

public class PactDslXmlBody extends DslPart {

    private JSONObject body = new JSONObject();

    public PactDslXmlBody(DslPart parent, String rootPath, String rootName) {
        super(parent, rootPath, rootName);
    }

    public PactDslXmlBody(String rootPath, String rootName) {
        super(rootPath, rootName);
    }

    public PactDslXmlBody() {
        super("", "");
    }

    @Override
    protected void putObject(DslPart object) {

    }

    @Override
    protected void putArray(DslPart object) {

    }

    @Override
    public Object getBody() {
        return body;
    }

    @Override
    public PactDslJsonArray array(String name) {
        return null;
    }

    @Override
    public PactDslJsonArray array() {
        return null;
    }

    @Override
    public DslPart closeArray() {
        return null;
    }

    @Override
    public PactDslJsonBody arrayLike(String name) {
        return null;
    }

    @Override
    public PactDslJsonBody arrayLike() {
        return null;
    }

    @Override
    public PactDslJsonBody eachLike(String name) {
        return null;
    }

    @Override
    public PactDslJsonBody eachLike() {
        return null;
    }

    @Override
    public PactDslJsonBody eachLike(String name, int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonBody eachLike(int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonBody minArrayLike(String name, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonBody minArrayLike(Integer size) {
        return null;
    }

    @Override
    public PactDslJsonBody minArrayLike(String name, Integer size, int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonBody minArrayLike(Integer size, int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonBody maxArrayLike(String name, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonBody maxArrayLike(Integer size) {
        return null;
    }

    @Override
    public PactDslJsonBody maxArrayLike(String name, Integer size, int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonBody maxArrayLike(Integer size, int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayLike(String name) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayLike() {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayLike(String name, int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayLike(int numberExamples) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMaxLike(String name, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMaxLike(Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMaxLike(String name, int numberExamples, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMaxLike(int numberExamples, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMinLike(String name, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMinLike(Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMinLike(String name, int numberExamples, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonArray eachArrayWithMinLike(int numberExamples, Integer size) {
        return null;
    }

    @Override
    public PactDslJsonBody object(String name) {
        return null;
    }

    @Override
    public PactDslJsonBody object() {
        return null;
    }

    @Override
    public DslPart closeObject() {
        return null;
    }

    @Override
    public DslPart close() {
        return null;
    }

    public <S, T extends S> PactDslXmlBody fromObject(T obj, Class<S> cls) {
        String variableName = "stringType";
        String variablePath = "." + variableName;

        body = buildBodyFromObject(obj, cls);
        body = initializeValues(body);

        addRules(body, ".");
        addGenerators(body,".");

        return this;
    }

    private void addGenerators(JSONObject object, String path) {
        for (String key : object.keySet()) {
            System.out.println("key:" + key);
            if (isAJsonObject(object.get(key))) {
                addGenerators(new JSONObject(object.get(key).toString()), path + key + ".");
            } else if ("string".equals(object.get(key))) {
                generators.addGenerator(Category.BODY, path + key, new RandomStringGenerator(20));
            }
        }
    }

    private void addRules(JSONObject object, String path) {
        for (String key : object.keySet()) {
            System.out.println("key:" + key);
            if (isAJsonObject(object.get(key))) {
                addRules(new JSONObject(object.get(key).toString()), path + key + ".");
            } else if ("string".equals(object.get(key))) {
                matchers.addRule(path + key, TypeMatcher.INSTANCE);
            }
        }
    }

    private <S, T extends S> JSONObject buildBodyFromObject(T obj, Class<S> cls) {
        StringWriter writer = new StringWriter();
        try {
            Configuration jsonConfig = JSONConverter.makeDefaultJSONConfig();
            JSONConverter.objToJSON(obj, cls, writer, jsonConfig);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return new JSONObject(writer.toString());
    }

    private JSONObject initializeValues(JSONObject object) {
        for (String key : object.keySet()) {
            if (isAJsonObject(object.get(key))) {
                JSONObject childObject = new JSONObject(object.get(key).toString());
                object.put(key, initializeValues(childObject));
            } else if (object.get(key) == null) {
                object.put(key, JSONObject.NULL);
            } else if ("".equals(object.get(key))) {
                object.put(key, "string");
            }
        }
        return object;
    }

    private boolean isAJsonObject(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
