
package country;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.cxf.annotations.Logging;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.CountriesPortService;

@Configuration
public class CountryConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this package must match the package in the <generatePackage> specified in
		// pom.xml
		
		marshaller.setContextPath("io.spring.guides.gs_producing_web_service");
		
		return marshaller;
	}

	@Bean
	public CountryClient quoteClient(Jaxb2Marshaller marshaller) {
		CountryClient client = new CountryClient();
		client.setDefaultUri("http://localhost:8080/ws");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

	
	@Bean
	public CountriesPort countryProxy() {
		JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
		jaxWsProxyFactoryBean.setServiceClass(CountriesPort.class);
		jaxWsProxyFactoryBean.setAddress(
				"http://localhost:8080/ws/");
		
		  // create the loggingInInterceptor and loggingOutInterceptor
        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);

        // add loggingInterceptor to print the received/sent messages
        jaxWsProxyFactoryBean.getInInterceptors().add(loggingInInterceptor);
        jaxWsProxyFactoryBean.getInFaultInterceptors()
                .add(loggingInInterceptor);
        jaxWsProxyFactoryBean.getOutInterceptors().add(loggingOutInterceptor);
        jaxWsProxyFactoryBean.getOutFaultInterceptors()
                .add(loggingOutInterceptor);
        
        
        Interceptor<? extends Message> intercept = new Interceptor<Message>() {
        	
			@Override
			public void handleMessage(Message message) throws Fault {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void handleFault(Message message) {
				// TODO Auto-generated method stub
				
			}
		};
		jaxWsProxyFactoryBean.getOutInterceptors().add(intercept);
		
//		Map<String,Object> properties = new HashMap<String,Object>();
//		 
//		// Create a mapping between the XML namespaces and the JSON prefixes.
//		// The JSON prefix can be "" to specify that you don't want any prefix.
//		HashMap<String, String> nstojns = new HashMap<String,String>();
//		nstojns.put("http://spring.io/guides/gs-producing-web-service", "prod");
//		MappedXMLInputFactory xif = new MappedXMLInputFactory(nstojns);
//		properties.put(XMLInputFactory.class.getName(), xif);
//		MappedXMLOutputFactory xof = new MappedXMLOutputFactory(nstojns);
//		properties.put(XMLOutputFactory.class.getName(), xof);
//		properties.put("Content-Type", "text/plain");	
//		//Build up the server factory bean
//		
//		//Use the HTTP Binding which understands the Java Rest Annotations
//		jaxWsProxyFactoryBean.setProperties(properties);
		return (CountriesPort) jaxWsProxyFactoryBean.create();
	}
	
}
