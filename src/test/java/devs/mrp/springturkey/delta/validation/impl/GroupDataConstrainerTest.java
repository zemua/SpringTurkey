package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import devs.mrp.springturkey.delta.validation.ModificationDelta;
import devs.mrp.springturkey.delta.validation.Table;

class GroupDataConstrainerTest {

	@Autowired
	@Qualifier("groupConstraints")

	@Test
	void test() {
		UUID id = UUID.randomUUID();
		ModificationDelta delta = ModificationDelta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.table(Table.GROUP)
				.recordId(id)
				.fieldName("preventClose")
				.textValue("true")
				.build();
		fail("Not yet implemented");
	}

}
