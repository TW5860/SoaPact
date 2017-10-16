package country;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

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
import org.json.JSONObject;
import org.json.XML;

public class XML2JSONConvertingReverseProxy extends ReverseProxy {

	public XML2JSONConvertingReverseProxy(String hostName, int port, String backServerURL) {
		super(hostName, port, backServerURL);
	}

	@Override
	protected String changeResponse(String string) {
		return jsonToXml(string);
	}
	
	@Override
	protected String changeRequest(InputStream inputStream) throws IOException {
		try {
			return xmlToJSON(inputStream);
		} catch (FactoryConfigurationError | XMLStreamException | TransformerFactoryConfigurationError
				| TransformerException e) {
			//TODO: Change error-handling
			throw (new IOException("This errormessage needs to be improved!"));
		}
	}

	private static String jsonToXml(String responseText) {
		JSONObject json = new JSONObject(responseText);
		String xml = XML.toString(json);
		return xml;
	}

	private static String xmlToJSON(InputStream inputStream) throws FactoryConfigurationError, XMLStreamException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
		if (inputStream.available() == -1)
			return "{}";
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

		Configuration jsonConfig = new Configuration();
		DefaultConverter jsonConverter = new DefaultConverter();
		jsonConverter.setEnforce32BitInt(true);
		jsonConfig.setTypeConverter(jsonConverter);
		StringWriter strWriter = new StringWriter();
		MappedNamespaceConvention jsonConvention = new MappedNamespaceConvention(jsonConfig);
		XMLStreamWriter writer = new MappedXMLStreamWriter(jsonConvention, strWriter);

		TransformerFactory.newInstance().newTransformer().transform(new StAXSource(reader), new StAXResult(writer));

		writer.close();
		strWriter.close();

		return strWriter.toString();
	}

}
