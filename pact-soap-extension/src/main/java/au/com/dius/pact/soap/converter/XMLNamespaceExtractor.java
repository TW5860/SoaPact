package au.com.dius.pact.soap.converter;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XMLNamespaceExtractor {

	public static Set<String> namespacesFromXML(String xml) {
		Set<String> namespaces = new HashSet<>();

		Reader reader = new StringReader(xml);
		XMLStreamReader xmlReader;
		try {
			xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
		} catch (XMLStreamException | FactoryConfigurationError e) {
			throw new RuntimeException(e);
		}

		try {
			while (xmlReader.hasNext()) {
			    processEvent(xmlReader, namespaces);
			    xmlReader.next();
			}
			processEvent(xmlReader, namespaces);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

		return namespaces;
	}

	private static void processEvent(XMLStreamReader xmlReader, Set<String> namespaces) {
		switch (xmlReader.getEventType()) {
		case XMLEvent.START_ELEMENT:
			int attribCount = xmlReader.getNamespaceCount();
			for (int i = 0; i < attribCount; i++) {
				namespaces.add(xmlReader.getNamespaceURI());
			}
		}
	}

}
