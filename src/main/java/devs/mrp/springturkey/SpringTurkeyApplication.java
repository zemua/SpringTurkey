package devs.mrp.springturkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SpringTurkeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTurkeyApplication.class, args);
	}

}
