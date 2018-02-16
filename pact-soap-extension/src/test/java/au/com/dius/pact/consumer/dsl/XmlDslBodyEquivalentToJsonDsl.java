package au.com.dius.pact.consumer.dsl;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XmlDslBodyEquivalentToJsonDsl {

    private PactDslJsonBody jsonBody;
    private PactDslXmlBody xmlBody;

    @Before
    public void setUp() throws Exception {
        jsonBody = new PactDslJsonBody();
        xmlBody = new PactDslXmlBody();
    }

    @Test
    public void shouldConvertEmptyRequestLikeJsonDoes(){
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
        jsonBody.object("objectToConvert").stringType("stringType").closeObject();
        ObjectToConvert objectWithARootString = new ObjectToConvert();
        objectWithARootString.stringType = "";
        OtherObjectToConvert otherObjectToConvert = new OtherObjectToConvert();
        otherObjectToConvert.myRandomString = "";

        xmlBody = xmlBody.fromObject(objectWithARootString, ObjectToConvert.class);
        assertEquality(jsonBody, xmlBody);

        jsonBody = new PactDslJsonBody();
        jsonBody.object("otherObjectToConvert").stringType("myRandomString").closeObject();
        xmlBody = new PactDslXmlBody();
        xmlBody = xmlBody.fromObject(otherObjectToConvert, OtherObjectToConvert.class);
        assertEquality(jsonBody, xmlBody);
    }

    private void assertEquality(PactDslJsonBody jsonBody, PactDslXmlBody xmlBody) {
        assertEquals(jsonBody.getBody().toString(),xmlBody.getBody().toString());
//        JSONAssert.assertEquals(jsonBody.getBody().toString(),xmlBody.getBody().toString(),false);
        assertEquals(jsonBody.getMatchers().toString(),xmlBody.getMatchers().toString());
        assertEquals(jsonBody.getGenerators(),xmlBody.getGenerators());
    }

    @XmlRootElement
    public static class ObjectToConvert{
        String stringType;

        public String getStringType() {
            return stringType;
        }

        public void setStringType(String stringType) {
            this.stringType = stringType;
        }
    }

    @XmlRootElement
    public static class OtherObjectToConvert{
        String myRandomString;

        public String getMyRandomString() {
            return myRandomString;
        }

        public void setMyRandomString(String myRandomString) {
            this.myRandomString = myRandomString;
        }
    }
}
