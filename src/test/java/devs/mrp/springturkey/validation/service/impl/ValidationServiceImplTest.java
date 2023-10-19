package devs.mrp.springturkey.validation.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.utils.impl.ObjectMapperProvider;
import devs.mrp.springturkey.validation.service.ValidationService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ValidationServiceImpl.class, ObjectMapperProvider.class})
class ValidationServiceImplTest {

	@Autowired
	private ValidationService validationService;

	@Test
	void testValidModification() throws WrongDataException {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.MODIFICATION)
				.jsonValue(Map.of("settingValue", "hello"))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		validationService.validate(delta);
	}

	@Test
	void testNonExistingFieldModification() throws WrongDataException {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.MODIFICATION)
				.jsonValue(Map.of("nonExistingField", "hello"))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		assertThrows(WrongDataException.class, () -> validationService.validate(delta));
	}

	@Test
	void testInvalidDataFieldModification() throws WrongDataException {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.MODIFICATION)
				.jsonValue(Map.of("settingValue", " "))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		assertThrows(WrongDataException.class, () -> validationService.validate(delta));
	}

	@Test
	void testValidCreation() throws WrongDataException {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.CREATION)
				.jsonValue(Map.of("settingValue", "hello", "settingKey", "bye", "platformType", "ALL"))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		validationService.validate(delta);
	}

	@Test
	void testCreationWithNonExistingFields() throws WrongDataException {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.CREATION)
				.jsonValue(Map.of("settingValue", "hello", "settingKey", "bye", "platformType", "ALL", "nonExisting", "something"))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		assertThrows(WrongDataException.class, () -> validationService.validate(delta));
	}

	@Test
	void testInvalidDataInFieldCreation() throws WrongDataException {
		Delta delta = Delta.builder()
				.deltaType(DeltaType.CREATION)
				.jsonValue(Map.of("settingValue", " ", "settingKey", "bye", "platformType", "ALL"))
				.recordId(UUID.randomUUID())
				.table(DeltaTable.SETTING)
				.timestamp(LocalDateTime.now())
				.build();

		assertThrows(WrongDataException.class, () -> validationService.validate(delta));
	}

}
