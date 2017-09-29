package country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

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

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReverseProxy {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static void main(String[] args) throws IOException, URISyntaxException {		
		Undertow backServer = Undertow.builder()
                .addHttpListener(8089, "localhost")
                .setHandler(new BlockingHandler((HttpServerExchange exchange) -> {
					InputStream inputStream = exchange.getInputStream();
					String bodyText = new BufferedReader(new InputStreamReader(inputStream))
							  .lines()
							  .collect(Collectors.joining("\n"));
					System.out.println("Back server: " + bodyText);
                }))
                .build();
		backServer.start();

		Undertow server = Undertow.builder()
                .addHttpListener(8088, "localhost")
                .setIoThreads(1)
                .setHandler(new BlockingHandler((HttpServerExchange exchange) -> {
					InputStream inputStream = exchange.getInputStream();
					String bodyText = xmlToJSON(inputStream);

					OkHttpClient client = new OkHttpClient();
					RequestBody body = RequestBody.create(JSON, bodyText);
					Request request = new Request.Builder()
							.url("http://localhost:8089/")
							.post(body)
							.build();
					Response response = client.newCall(request).execute();
					System.out.println("front: " + response.body().string());
		
					exchange.getResponseSender().send("Yeah!");
				}))
                .build();

		server.start();
	}

	private static String xmlToJSON(InputStream inputStream) throws FactoryConfigurationError, XMLStreamException,
			TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
		XMLStreamReader reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(inputStream);
		
		Configuration jsonConfig = new Configuration();
        DefaultConverter jsonConverter = new DefaultConverter();
        jsonConverter.setEnforce32BitInt(true);
        jsonConfig.setTypeConverter(jsonConverter);
        StringWriter strWriter = new StringWriter();
        MappedNamespaceConvention jsonConvention = new MappedNamespaceConvention(jsonConfig);
        XMLStreamWriter writer = new MappedXMLStreamWriter(jsonConvention, strWriter);
 
        TransformerFactory.newInstance()
        		.newTransformer()
        		.transform(new StAXSource(reader), new StAXResult(writer));
        
        writer.close();
        strWriter.close();
 
        return strWriter.toString();
	}
}
