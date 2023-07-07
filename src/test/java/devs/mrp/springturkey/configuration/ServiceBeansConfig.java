package devs.mrp.springturkey.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"devs.mrp.springturkey.database.service", "devs.mrp.springturkey.components"})
public class ServiceBeansConfig {

}
