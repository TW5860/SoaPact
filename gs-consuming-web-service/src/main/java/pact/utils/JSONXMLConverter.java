package pact.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.DefaultConverter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class JSONXMLConverter {
	private static Configuration defaultJSONConfig;

	static {
		defaultJSONConfig = new Configuration();
	}

	public static Configuration getDefaultJSONConfig() {
		return defaultJSONConfig;
	}

	public static void xmlToJSON(Reader reader, Writer writer) throws FactoryConfigurationError, XMLStreamException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
		xmlToJSON(reader, writer, getDefaultJSONConfig());
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

		TransformerFactory.newInstance().newTransformer().transform(new StAXSource(xmlReader),
				new StAXResult(xmlWriter));

		xmlWriter.close();
	}
}
