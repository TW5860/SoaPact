package country;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.spring.guides.gs_producing_web_service.CountriesPort;

@SuppressWarnings("deprecation")
@Configuration
public class CountryConfiguration {
	
	@Bean(name="endPoint")
	public String getEndPoint() {
		return "http://localhost:8080/ws/";
	}
	
	@Bean
	public CountriesPort countryPort(@Qualifier("endPoint") String endPoint) {
		return getCountriesPort(endPoint);
	}

	public static CountriesPort getCountriesPort(String endPoint) {
		JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
		jaxWsProxyFactoryBean.setServiceClass(CountriesPort.class);
		jaxWsProxyFactoryBean.setAddress(endPoint);

		// create the loggingInInterceptor and loggingOutInterceptor
//		LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
//		loggingInInterceptor.setPrettyLogging(true);
//		LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
//		loggingOutInterceptor.setPrettyLogging(true);

		// add loggingInterceptor to print the received/sent messages
//		jaxWsProxyFactoryBean.getInInterceptors().add(loggingInInterceptor);
//		jaxWsProxyFactoryBean.getInFaultInterceptors().add(loggingInInterceptor);
//		jaxWsProxyFactoryBean.getOutInterceptors().add(loggingOutInterceptor);
//		jaxWsProxyFactoryBean.getOutFaultInterceptors().add(loggingOutInterceptor);
		return (CountriesPort) jaxWsProxyFactoryBean.create();
	}
}

