package devs.mrp.springturkey.utils.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class ObjectMapperProvider {

	@Bean
	public ObjectMapper getObjectMapper() {
		return JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();
	}

}
