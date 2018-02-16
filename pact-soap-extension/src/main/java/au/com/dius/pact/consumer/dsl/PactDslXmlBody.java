package au.com.dius.pact.consumer.dsl;

import au.com.dius.pact.model.generators.Category;
import au.com.dius.pact.model.generators.RandomIntGenerator;
import au.com.dius.pact.model.generators.RandomStringGenerator;
import au.com.dius.pact.model.matchingrules.NumberTypeMatcher;
import au.com.dius.pact.model.matchingrules.TypeMatcher;
import au.com.dius.pact.soap.converter.JSONConverter;
import org.codehaus.jettison.mapped.Configuration;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

public class PactDslXmlBody extends DslPart {

    private static final int INT_DEFAULT_VALUE = 100;
    private static final String STRING_DEFAULT_VALUE = "string";
    private static final boolean BOOLEAN_DEFAULT_VALUE = true;
    private static final String ROOT_PATH = ".";

    protected Configuration jsonConfig;
    protected JSONObject body = new JSONObject();

    //region Constructor
    public PactDslXmlBody(DslPart parent, String rootPath, String rootName) {
        super(parent, rootPath, rootName);
    }

    public PactDslXmlBody(String rootPath, String rootName) {
        super(rootPath, rootName);
    }

    public PactDslXmlBody() {
        super("", "");
        jsonConfig = JSONConverter.makeDefaultJSONConfig();
    }

    //endregion

    public <S, T extends S> PactDslXmlBody fromObject(T obj, Class<S> cls) {
        String variableName = "stringType";
        String variablePath = makeQualifiedPath(ROOT_PATH, variableName);

        body = buildBodyFromObject(body, obj, cls);
        body = initializeValues(body);

        addRules(body, ROOT_PATH);
        addGenerators(body, ROOT_PATH);
        closed = true;
        return this;
    }

    @NotNull
    protected String makeQualifiedPath(String path, String key) {
        return path + key;
    }

    private <S, T extends S> JSONObject buildBodyFromObject(JSONObject base, T obj, Class<S> cls) {
        StringWriter writer = new StringWriter();
        try {
            JSONConverter.objToJSON(obj, cls, writer, jsonConfig);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(writer.toString());

        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            base.put(key, value != null ? value : JSONObject.NULL);
        }

        return base;
    }

    private void addGenerators(JSONObject object, String path) {
        for (String key : object.keySet()) {
            if (isAJsonObject(object.get(key))) {
                addGenerators(new JSONObject(object.get(key).toString()), makeQualifiedPath(path, key) + ROOT_PATH);
            } else if (STRING_DEFAULT_VALUE.equals(object.get(key))) {
                generators.addGenerator(Category.BODY, makeQualifiedPath(path, key), new RandomStringGenerator(20));
            } else if (object.get(key).equals(INT_DEFAULT_VALUE)) {
                generators.addGenerator(Category.BODY, makeQualifiedPath(path, key), new RandomIntGenerator(0, Integer.MAX_VALUE));
            }
        }
    }

    private void addRules(JSONObject object, String path) {
        for (String key : object.keySet()) {
            if (isAJsonObject(object.get(key))) {
                addRules(new JSONObject(object.get(key).toString()), path + key + ROOT_PATH);
            } else if (STRING_DEFAULT_VALUE.equals(object.get(key))) {
                matchers.addRule(makeQualifiedPath(path, key), TypeMatcher.INSTANCE);
            } else if ("java.lang.Integer".equals(object.get(key).getClass().getName())) {
                matchers.addRule(makeQualifiedPath(path, key), new NumberTypeMatcher(NumberTypeMatcher.NumberType.INTEGER));
            } else if ("java.lang.Float".equals(object.get(key).getClass().getName())
                    || "java.lang.Double".equals(object.get(key).getClass().getName())) {
                matchers.addRule(makeQualifiedPath(path, key), new NumberTypeMatcher(NumberTypeMatcher.NumberType.DECIMAL));
            } else if ("java.lang.Boolean".equals(object.get(key).getClass().getName())) {
                matchers.addRule(makeQualifiedPath(path, key), TypeMatcher.INSTANCE);
            }
        }
    }

    private JSONObject initializeValues(JSONObject object) {
        for (String key : object.keySet()) {
            if (isAJsonObject(object.get(key))) {
                JSONObject childObject = new JSONObject(object.get(key).toString());
                object.put(key, initializeValues(childObject));
            } else if (object.get(key) == null) {
                object.put(key, JSONObject.NULL);
            } else if ("".equals(object.get(key))) {
                object.put(key, STRING_DEFAULT_VALUE);
            } else if (object.get(key).equals(0)) {
                object.put(key, INT_DEFAULT_VALUE);
            } else if (object.get(key).equals(false)) {
                object.put(key, BOOLEAN_DEFAULT_VALUE);
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

    public String toString() {
        return body.toString();
    }

    //region for compatibility
    @Override
    @Deprecated
    protected void putObject(DslPart object) {

    }

    @Override
    @Deprecated
    protected void putArray(DslPart object) {

    }

    @Override
    public Object getBody() {
        return body;
    }

    @Override
    @Deprecated
    public PactDslJsonArray array(String name) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray array() {
        return null;
    }

    @Override
    @Deprecated
    public DslPart closeArray() {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody arrayLike(String name) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody arrayLike() {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody eachLike(String name) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody eachLike() {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody eachLike(String name, int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody eachLike(int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody minArrayLike(String name, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody minArrayLike(Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody minArrayLike(String name, Integer size, int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody minArrayLike(Integer size, int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody maxArrayLike(String name, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody maxArrayLike(Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody maxArrayLike(String name, Integer size, int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody maxArrayLike(Integer size, int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayLike(String name) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayLike() {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayLike(String name, int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayLike(int numberExamples) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMaxLike(String name, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMaxLike(Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMaxLike(String name, int numberExamples, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMaxLike(int numberExamples, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMinLike(String name, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMinLike(Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMinLike(String name, int numberExamples, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonArray eachArrayWithMinLike(int numberExamples, Integer size) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody object(String name) {
        return null;
    }

    @Override
    @Deprecated
    public PactDslJsonBody object() {
        return null;
    }

    @Override
    @Deprecated
    public DslPart closeObject() {
        return null;
    }

    @Override
    public DslPart close() {
        this.getMatchers().applyMatcherRootPrefix("$");
        this.getGenerators().applyRootPrefix("$");
        return this;
    }

    //#endregion

}
