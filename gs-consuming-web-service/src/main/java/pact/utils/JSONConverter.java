package pact.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.DefaultConverter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
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

	public static String xmlToJSON(String xml) throws Exception {
		Reader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();
	
		xmlToJSON(reader, writer);
	
		return writer.toString();
	}

	public static void xmlToJSON(Reader reader, Writer writer, Configuration jsonConfig)
			throws FactoryConfigurationError, XMLStreamException, TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException, IOException {
		XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
		XMLStreamWriter xmlWriter = makeJSONXMLStreamWriter(writer, jsonConfig);

		xmlReaderToWriter(xmlReader, xmlWriter);

		xmlWriter.close();
	}

	public static String xmlToJSON(String xml, Configuration jsonConfig) throws Exception {
		Reader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();
	
		xmlToJSON(reader, writer, jsonConfig);
	
		return writer.toString();
	}

	private static void xmlReaderToWriter(XMLStreamReader xmlReader, XMLStreamWriter xmlWriter)
			throws XMLStreamException {
		while (xmlReader.hasNext()) {
            copyEvent(xmlReader, xmlWriter);
            xmlReader.next();
        }
        copyEvent(xmlReader, xmlWriter);
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
	
	public static void jsonToXML(String jsonText, Writer writer)
			throws JSONException, XMLStreamException {
		jsonToXML(jsonText, writer, defaultJSONConfig);
	}
	
	public static String jsonToXML(String json) throws JSONException, XMLStreamException {
		StringWriter writer = new StringWriter();
		jsonToXML(json, writer);
		return writer.toString();
	}

	public static void jsonToXML(String jsonText, Writer writer, Configuration jsonConfig)
			throws JSONException, XMLStreamException {
		// Jettison includes its own version of the JSONObject class :-(
		org.codehaus.jettison.json.JSONObject jsonObj = new org.codehaus.jettison.json.JSONObject(jsonText);
		MappedNamespaceConvention con = new MappedNamespaceConvention(jsonConfig);
		XMLStreamReader xmlReader = new MappedXMLStreamReader(jsonObj, con);
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(writer);
		
		xmlReaderToWriter(xmlReader, xmlWriter);
		
		xmlWriter.close();
	}

	public static String jsonToXML(String json, Configuration jsonConfig) throws JSONException, XMLStreamException {
		StringWriter writer = new StringWriter();
		jsonToXML(json, writer, jsonConfig);
		return writer.toString();
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

	public static <S, T extends S> String objToJSON(T obj, Class<S> cls,
			Configuration jsonConfig) throws Exception {
		StringWriter writer = new StringWriter();
		objToJSON(obj, cls, writer, jsonConfig);
		return writer.toString();
	}
}