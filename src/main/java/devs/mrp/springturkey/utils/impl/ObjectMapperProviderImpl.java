package devs.mrp.springturkey.utils.impl;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.utils.ObjectMapperProvider;

@Component
public class ObjectMapperProviderImpl implements ObjectMapperProvider {

	@Override
	public ObjectMapper getObjectMapper() {
		return JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();
	}

}
