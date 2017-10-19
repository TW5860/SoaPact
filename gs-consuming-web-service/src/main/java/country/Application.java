
package country;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	CommandLineRunner lookup(CountriesPort countriesPort) {
		return args -> {
			String country = "Spain";

			if (args.length > 0) {
				country = args[0];
			}
			
			Logger logger = LoggerFactory.getLogger(Application.class);
		  
			GetCountryRequest request = new GetCountryRequest();
			request.setName(country);
			GetCountryResponse response = countriesPort.getCountry(request);

			logger.info("------------------ %s ------------------",response.getCountry().getCapital());
		};
	}

}
