package au.com.dius.pact.consumer.dsl;

import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by kimfehrs on 16/02/18.
 */
public class PactDslSoapBody2Test {

    @Test
    public void shouldCreateSoapRequestFromObject(){
        ObjectToConvertToSoap obj = new ObjectToConvertToSoap();
        PactDslSoapBody2 soapBody = new PactDslSoapBody2();
        soapBody.withNs("http:localhost:8080")
                .fromObject(obj,ObjectToConvertToSoap.class);

        System.out.println(soapBody.getBody().toString());

        DslPart responseForAnExistingCountry = new PactDslSoapBody()
                .withNs("http:localhost:8080")
                .fromObject(obj,ObjectToConvertToSoap.class);

        System.out.println(responseForAnExistingCountry.getBody().toString());
    }

    @XmlRootElement
    public static class ObjectToConvertToSoap {
        String stringType;
        int intType;
        float floati;
        boolean wahrOderNicht;
        XmlDslBodyEquivalentToJsonDsl.ObjectToConvert obj;
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

        public XmlDslBodyEquivalentToJsonDsl.ObjectToConvert getObj() {
            return obj;
        }

        public void setObj(XmlDslBodyEquivalentToJsonDsl.ObjectToConvert obj) {
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
}