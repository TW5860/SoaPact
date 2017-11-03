package pact.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.DefaultConverter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class JSONXMLConverter {
	private static Configuration defaultJSONConfig;

	public static Configuration makeDefaultJSONConfig() {
		Configuration config = new Configuration();
		config.setJsonNamespaceSeparator("#");
		
		return config;
	}
	
	static {
		defaultJSONConfig = makeDefaultJSONConfig();
	}

	public static void xmlToJSON(Reader reader, Writer writer) throws FactoryConfigurationError, XMLStreamException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
		xmlToJSON(reader, writer, defaultJSONConfig);
	}

	public static void xmlToJSON(Reader reader, Writer writer, Configuration jsonConfig)
			throws FactoryConfigurationError, XMLStreamException, TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException, IOException {
		XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);

		DefaultConverter jsonConverter = new DefaultConverter();
		jsonConverter.setEnforce32BitInt(true);
		jsonConfig.setTypeConverter(jsonConverter);

		MappedNamespaceConvention jsonConvention = new MappedNamespaceConvention(jsonConfig);
		XMLStreamWriter xmlWriter = new MappedXMLStreamWriter(jsonConvention, writer);

		while (xmlReader.hasNext()) {
            copyEvent(xmlReader, xmlWriter);
            xmlReader.next();
        }
        copyEvent(xmlReader, xmlWriter);

		xmlWriter.close();
	}
	
	private static void copyEvent(XMLStreamReader xmlReader, XMLStreamWriter xmlWriter) throws XMLStreamException {
		switch (xmlReader.getEventType()) {
		case XMLEvent.START_DOCUMENT:
			xmlWriter.writeStartDocument("1.0");
			break;
		case XMLEvent.END_DOCUMENT:
			xmlWriter.writeEndDocument();
			break;
		case XMLEvent.START_ELEMENT:
			xmlWriter.writeStartElement(
					xmlReader.getPrefix(),
					xmlReader.getLocalName(),
					xmlReader.getNamespaceURI());
			break;
		case XMLEvent.END_ELEMENT:
			xmlWriter.writeEndElement();
			break;
		case XMLEvent.SPACE:
			break;
		case XMLEvent.CHARACTERS:
			xmlWriter.writeCharacters(xmlReader.getTextCharacters(), xmlReader.getTextStart(), xmlReader.getTextLength());
			break;
		case XMLEvent.CDATA:
			xmlWriter.writeCData(xmlReader.getText());
			break;
		}
	}
}
