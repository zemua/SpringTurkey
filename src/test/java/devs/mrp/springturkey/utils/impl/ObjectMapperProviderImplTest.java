package devs.mrp.springturkey.utils.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.springturkey.utils.ObjectMapperProvider;
import lombok.Getter;
import lombok.Setter;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ObjectMapperProviderImpl.class})
class ObjectMapperProviderImplTest {

	@Autowired
	private ObjectMapperProvider objectMapperProvider;

	@Test
	void testMapping() {
		Mapeable mapeable = new Mapeable();
		mapeable.myField = "hello";
		mapeable.timeField = LocalDateTime.of(2020, 1, 1, 0, 0);

		ObjectMapper mapper = objectMapperProvider.getObjectMapper();
		Map<String,Object> mapped = mapper.convertValue(mapeable, Map.class);

		assertEquals("hello", mapped.get("myField"));
		assertEquals(List.of(2020, 1, 1, 0, 0), mapped.get("timeField"));
	}

	@Getter
	@Setter
	private static class Mapeable {
		String myField;
		LocalDateTime timeField;
	}

}
