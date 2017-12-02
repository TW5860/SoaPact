package pact.utils.converter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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
	private static final String JSON_NAMESPACE_SEPARATOR = "#";

	private static Configuration defaultJSONConfig;
	private static Map<String, String> emptyXMLToJsonNamespaces = new HashMap<String, String>();

	public static Configuration makeDefaultJSONConfig() {
		Configuration jsonConfig = new Configuration();
		jsonConfig.setJsonNamespaceSeparator(JSON_NAMESPACE_SEPARATOR);
		
		DefaultConverter jsonConverter = new DefaultConverter();
		jsonConverter.setEnforce32BitInt(true);
		jsonConfig.setTypeConverter(jsonConverter);

		return jsonConfig;
	}
	
	static {
		defaultJSONConfig = makeDefaultJSONConfig();
	}

	public static void xmlToJSON(Reader reader, Writer writer) throws FactoryConfigurationError, XMLStreamException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException, JSONException {
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
			TransformerConfigurationException, TransformerException, IOException, JSONException {
		XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
		XMLStreamWriter xmlWriter = makeJSONXMLStreamWriter(writer, jsonConfig);

		xmlReaderToWriter(xmlReader, xmlWriter, emptyXMLToJsonNamespaces);

		xmlWriter.close();
	}

	public static String xmlToJSON(String xml, Configuration jsonConfig) throws Exception {
		Reader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();
	
		xmlToJSON(reader, writer, jsonConfig);
	
		return writer.toString();
	}

	private static void xmlReaderToWriter(XMLStreamReader xmlReader, XMLStreamWriter xmlWriter, Map<String, String> xmlToJsonNamespaces)
			throws XMLStreamException, JSONException {
		boolean firstElem = true;
		while (xmlReader.hasNext()) {
            firstElem = copyEvent(xmlReader, xmlWriter, firstElem, xmlToJsonNamespaces);
            xmlReader.next();
        }
        copyEvent(xmlReader, xmlWriter, firstElem, xmlToJsonNamespaces);
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
		
		xmlReaderToWriter(xmlReader, xmlWriter, (Map<String,String>)jsonConfig.getXmlToJsonNamespaces());
		
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
	
	private static boolean copyEvent(XMLStreamReader xmlReader, XMLStreamWriter xmlWriter,
			boolean firstElem, Map<String, String> xmlToJsonNamespaces) throws XMLStreamException, JSONException {
		switch (xmlReader.getEventType()) {
		case XMLEvent.START_DOCUMENT:
			xmlWriter.writeStartDocument("1.0");
			break;
		case XMLEvent.END_DOCUMENT:
			xmlWriter.writeEndDocument();
			break;
		case XMLEvent.START_ELEMENT:
			String prefix = xmlReader.getPrefix();
			String localName = xmlReader.getLocalName();
			String namespaceURI = xmlReader.getNamespaceURI();

			if (namespaceURI != null && !namespaceURI.equals("")) {
				String configPrefix = xmlToJsonNamespaces.get(namespaceURI);
				if (configPrefix != null) {
					prefix = configPrefix;
				}
				xmlWriter.writeStartElement(prefix, localName, namespaceURI);
			} else {
				if (localName.contains(JSON_NAMESPACE_SEPARATOR)) {
					throw new JSONException("Namespace prefix not found for element '" + localName + "'");
				}
				xmlWriter.writeStartElement(localName);
			}
			if (firstElem) {
				for (Map.Entry<String, String> entry: xmlToJsonNamespaces.entrySet()) {					
					xmlWriter.writeNamespace(entry.getValue(), entry.getKey());
				}
				firstElem = false;
			}
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
		
		return firstElem;
	}

	public static <S, T extends S> String objToJSON(T obj, Class<S> cls,
			Configuration jsonConfig) throws Exception {
		StringWriter writer = new StringWriter();
		objToJSON(obj, cls, writer, jsonConfig);
		return writer.toString();
	}
}