package pact.utils.converter;
//package pact.utils;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.StringWriter;
//
//import javax.xml.parsers.FactoryConfigurationError;
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLStreamReader;
//import javax.xml.stream.XMLStreamWriter;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.TransformerFactoryConfigurationError;
//import javax.xml.transform.stax.StAXResult;
//import javax.xml.transform.stax.StAXSource;
//
//import org.codehaus.jettison.badgerfish.BadgerFishDOMDocumentParser;
//import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
//import org.codehaus.jettison.mapped.MappedNamespaceConvention;
//import org.w3c.dom.Document;
//
//import com.jayway.jsonpath.Configuration;
//
//import net.minidev.asm.DefaultConverter;
//
//public class BadgerfishConverter {
//	public static String jsonToXml(String json) {
//		return "";
//	}
//
//	// private String toJSON(Element srcDOM) throws Exception {
//	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	// new BadgerFishDOMDocumentSerializer(baos).serialize(srcDOM);
//	// return new String(baos.toByteArray());
//	// }
//
//	private String toXML(String jsonStr) throws Exception {
//		ByteArrayInputStream bais = new ByteArrayInputStream(jsonStr.getBytes());
//		Document resDOM = new BadgerFishDOMDocumentParser().parse(bais);
//		// return printNode(resDOM);
//		return "";
//	}
//
//	public static String xmlToJSON(InputStream inputStream) throws FactoryConfigurationError, XMLStreamException,
//			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
//		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
//
//		Configuration jsonConfig = new Configuration();
//		DefaultConverter jsonConverter = new DefaultConverter();
//		jsonConverter.setEnforce32BitInt(true);
//		jsonConfig.setTypeConverter(jsonConverter);
//		StringWriter strWriter = new StringWriter();
//		MappedNamespaceConvention jsonConvention = new MappedNamespaceConvention(jsonConfig);
//		XMLStreamWriter writer = new BadgerFishXMLStreamWriter(strWriter);
//
//		TransformerFactory.newInstance().newTransformer().transform(new StAXSource(reader), new StAXResult(writer));
//
//		writer.close();
//		strWriter.close();
//
//		return strWriter.toString();
//	}
//
//}
