package devs.mrp.springturkey.validation.service.impl;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.validation.service.ValidationService;

@ExtendWith(SpringExtension.class)
class ValidationServiceImplTest {

	@Autowired
	private ValidationService validationService;

	@Test
	void testValid() {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.MODIFICATION)
				.jsonValue(Map.of("settingValue", "hello"))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		fail("not yet implemented");
	}

}
