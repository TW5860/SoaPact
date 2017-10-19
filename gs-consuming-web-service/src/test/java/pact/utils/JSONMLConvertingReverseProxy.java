package pact.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.cxf.helpers.IOUtils;

public class JSONMLConvertingReverseProxy extends ReverseProxy {
	
	public JSONMLConvertingReverseProxy(String hostName, int port, String backServerURL) {
		super(hostName, port, backServerURL);
	}

	@Override
	protected String changeResponse(String string) {
		return JSONMLConverter.jsonToXml(string);
	}
	
	@Override
	protected String changeRequest(InputStream inputStream) throws IOException {
		try {
			return xmlToJSON(inputStream);
		} catch (FactoryConfigurationError | TransformerFactoryConfigurationError e) {
			//TODO: Change error-handling
			throw (new IOException("This errormessage needs to be improved!"));
		}
	}

	public static String xmlToJSON(InputStream inputStream) throws IOException {
		if (inputStream.available() == -1)
			return "{}";
		
		String xml = IOUtils.toString(inputStream); 
		return JSONMLConverter.xmlToJSON(xml);
	}
}
