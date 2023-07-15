package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SettingDataConstrainer.class})
class SettingDataConstrainerTest {

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("settingConstraints")
	private DataConstrainer dataConstrainer;

	private static Stream<Arguments> provideCorrectValues() {
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "setting_value", "setting value 12345")
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void pushesCorrectly(DeltaType deltaType, DeltaTable table, String fieldName, String columnName, String textValue) throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(deltaType)
				.table(table)
				.recordId(id)
				.fieldName(fieldName)
				.textValue(textValue)
				.build();

		Delta modifiedDelta = delta.withFieldName(columnName);

		when(deltaFacade.pushDelta(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	private static Stream<Arguments> provideIncorrectValues() {
		// this doesn't throw exception
		// Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "setting value 12345")
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "settingValue", "setting value 12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "invalid", "setting value 12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "setting value 12345 $")
				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void pushesFails(DeltaType deltaType, DeltaTable table, String fieldName, String textValue) throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(deltaType)
				.table(table)
				.recordId(id)
				.fieldName(fieldName)
				.textValue(textValue)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

}
