package au.com.dius.pact.consumer.dsl;

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class XmlDslBodyEquivalentToJsonDsl {

    private PactDslJsonBody jsonBody;
    private PactDslXmlBody xmlBody;

    @Before
    public void setUp() throws Exception {
        jsonBody = new PactDslJsonBody();
        xmlBody = new PactDslXmlBody();
    }

    @Test
    public void shouldConvertEmptyRequestLikeJsonDoes() {
        assertEquality(jsonBody, xmlBody);
    }

    @Test(expected = Exception.class)
    public void cantConvertWithoutRootObject() throws Exception {
        jsonBody.stringType("stringType");
        String stringType = null;
        xmlBody = xmlBody.fromObject(stringType, String.class);
    }

    @Test
    public void shouldConvertAnyObject() throws Exception {
        jsonBody.object("objectToConvert")
                .integerType("intType")
                .booleanType("wahrOderNicht")
                .decimalType("floati", 5.3)
//                .stringType("stringType", "hallo")
                .stringValue("stringType", "hallo")
                .object("obj")
                .integerType("intType")
                .booleanType("wahrOderNicht")
                .decimalType("floati", 5.3)
                .stringType("stringType")
                .closeObject()
                .array("list")
                .string("hallo")
                .string("ihr")
                .closeArray()
                .closeObject();

        ObjectToConvert rootObject = new ObjectToConvert();
        rootObject.stringType = "hallo";
        rootObject.floati = 5.3f;
        rootObject.obj = new ObjectToConvert();
        rootObject.obj.stringType = "";
        rootObject.obj.floati = 5.3f;
        rootObject.list = new ArrayList<>();
        rootObject.list.add("hallo");
        rootObject.list.add("ihr");

        OtherObjectToConvert otherObjectToConvert = new OtherObjectToConvert();
        otherObjectToConvert.myRandomString = "";

        xmlBody = xmlBody.fromObject(rootObject, ObjectToConvert.class);
        assertEquality(jsonBody, xmlBody);

        jsonBody = new PactDslJsonBody();
        jsonBody.object("otherObjectToConvert").integerType("randomInt").stringType("myRandomString").closeObject();
        xmlBody = new PactDslXmlBody();
        xmlBody = xmlBody.fromObject(otherObjectToConvert, OtherObjectToConvert.class);
        assertEquality(jsonBody, xmlBody);
    }

    private void assertEquality(PactDslJsonBody jsonBody, PactDslXmlBody xmlBody) {
//        assertEquals(jsonBody.getBody().toString(), xmlBody.getBody().toString());
        JSONAssert.assertEquals(jsonBody.getBody().toString(),xmlBody.getBody().toString(),true);
        System.out.println(jsonBody.getBody().toString());
        if (jsonBody.getMatchers().allMatchingRules().size() > 1) {
            assertThat(xmlBody.getMatchers().allMatchingRules(),
                    containsInAnyOrder(jsonBody.getMatchers().allMatchingRules().toArray()));
        } else {
            assertEquals(jsonBody.getMatchers().allMatchingRules(), xmlBody.getMatchers().allMatchingRules());
        }
        assertEquals(jsonBody.getGenerators(), xmlBody.getGenerators());
    }


    @XmlRootElement
    public static class ObjectToConvert {
        String stringType;
        int intType;
        float floati;
        boolean wahrOderNicht;
        ObjectToConvert obj;
        List<String> list;

        public boolean isWahrOderNicht() {
            return wahrOderNicht;
        }

        public void setWahrOderNicht(boolean wahrOderNicht) {
            this.wahrOderNicht = wahrOderNicht;
        }

        public String getStringType() {
            return stringType;
        }

        public void setStringType(String stringType) {
            this.stringType = stringType;
        }

        public int getIntType() {
            return intType;
        }

        public void setIntType(int intType) {
            this.intType = intType;
        }

        public ObjectToConvert getObj() {
            return obj;
        }

        public void setObj(ObjectToConvert obj) {
            this.obj = obj;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public float getFloati() {
            return floati;
        }

        public void setFloati(float floati) {
            this.floati = floati;
        }
    }

    @XmlRootElement
    public static class OtherObjectToConvert {
        String myRandomString;
        int randomInt;

        public String getMyRandomString() {
            return myRandomString;
        }

        public void setMyRandomString(String myRandomString) {
            this.myRandomString = myRandomString;
        }

        public int getRandomInt() {
            return randomInt;
        }

        public void setRandomInt(int randomInt) {
            this.randomInt = randomInt;
        }
    }
}
