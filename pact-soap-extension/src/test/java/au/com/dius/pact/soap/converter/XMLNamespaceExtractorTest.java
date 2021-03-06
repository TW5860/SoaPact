package au.com.dius.pact.soap.converter;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

import java.util.Set;

import au.com.dius.pact.consumer.dsl.FileReader;
import org.junit.Test;

public class XMLNamespaceExtractorTest {
	@Test
	public void namespacesFromXML_extractsTheNamespacesFromASimpleXML()  {
		String xml = FileReader.readFile("ValidSoapRequest.xml");

		Set<String> namespaces = XMLNamespaceExtractor.namespacesFromXML(xml);

		assertThat(namespaces.size(), equalTo(2));
		assertThat("http://schemas.xmlsoap.org/soap/envelope/", isIn(namespaces));
		assertThat("http://spring.io/guides/gs-producing-web-service", isIn(namespaces));
	}

	@Test
	public void namespacesFromXML_returnsEmptySetOnXMLWithoutNamespaces()  {
		String xml = "<a></a>";

		Set<String> namespaces = XMLNamespaceExtractor.namespacesFromXML(xml);

		assertThat(namespaces.size(), equalTo(0));
	}
}
