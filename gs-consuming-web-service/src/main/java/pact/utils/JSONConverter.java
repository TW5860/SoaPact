package pact.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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

public class JSONConverter {
	private static Configuration defaultJSONConfig;

	public static Configuration makeDefaultJSONConfig() {
		Configuration jsonConfig = new Configuration();
		jsonConfig.setJsonNamespaceSeparator("#");
		
		DefaultConverter jsonConverter = new DefaultConverter();
		jsonConverter.setEnforce32BitInt(true);
		jsonConfig.setTypeConverter(jsonConverter);

		return jsonConfig;
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
		XMLStreamWriter xmlWriter = makeJSONXMLStreamWriter(writer, jsonConfig);

		while (xmlReader.hasNext()) {
            copyEvent(xmlReader, xmlWriter);
            xmlReader.next();
        }
        copyEvent(xmlReader, xmlWriter);

		xmlWriter.close();
	}
	
	public static <S, T extends S> void objToJSON(T obj, Class<S> cls, Writer writer) throws JAXBException {
		objToJSON(obj, cls, writer, defaultJSONConfig);
	}

	public static <S, T extends S> void objToJSON(T obj, Class<S> cls, Writer writer,
			Configuration jsonConfig) throws JAXBException {
		XMLStreamWriter xmlWriter = makeJSONXMLStreamWriter(writer, jsonConfig);
		JAXBContext jc = JAXBContext.newInstance(cls);
		Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(obj, xmlWriter);
	}

	private static XMLStreamWriter makeJSONXMLStreamWriter(Writer writer, Configuration jsonConfig) {
		MappedNamespaceConvention jsonConvention = new MappedNamespaceConvention(jsonConfig);
		XMLStreamWriter xmlWriter = new MappedXMLStreamWriter(jsonConvention, writer);
		return xmlWriter;
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
