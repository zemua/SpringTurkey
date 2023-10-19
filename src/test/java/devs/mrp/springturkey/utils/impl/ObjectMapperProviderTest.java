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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ObjectMapperProvider.class})
class ObjectMapperProviderTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testMapping() {
		Mapeable mapeable = new Mapeable();
		mapeable.myField = "hello";
		mapeable.timeField = LocalDateTime.of(2020, 1, 1, 0, 0);

		Map<String,Object> mapped = objectMapper.convertValue(mapeable, Map.class);

		assertEquals("hello", mapped.get("myField"));
		assertEquals(List.of(2020, 1, 1, 0, 0), mapped.get("timeField"));

		Mapeable reMapped = objectMapper.convertValue(mapped, Mapeable.class);
		assertEquals(mapeable, reMapped);
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	private static class Mapeable {
		String myField;
		LocalDateTime timeField;
	}

}
