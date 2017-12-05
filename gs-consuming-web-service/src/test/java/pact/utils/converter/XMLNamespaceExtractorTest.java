package pact.utils.converter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

import pact.utils.FileReader;

public class XMLNamespaceExtractorTest {
	@Test
	public void namespacesFromXML_extractsTheNamespacesFromASimpleXML()  {
		String soapRequestInXML = FileReader.readFile("ValidSoapRequest.xml");

		Set<String> namespaces = XMLNamespaceExtractor.namespacesFromXML(soapRequestInXML);

		assertThat(namespaces.size(), equalTo(2));
		assertThat("http://schemas.xmlsoap.org/soap/envelope/", isIn(namespaces));
		assertThat("http://spring.io/guides/gs-producing-web-service", isIn(namespaces));
	}
}
