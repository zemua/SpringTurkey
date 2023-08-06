package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
@ContextConfiguration(classes = {ModificationDataConstrainer.class})
class ModificationDataConstrainerTest {

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("modificationConstraints")
	private DataConstrainer dataConstrainer;

	private static Stream<Arguments> provideCorrectValues() {
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "required_usage_ms", "12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", "last_days_to_consider", "3"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "conditionalGroup", "conditional_group", "some group name 123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "name", "name", "some group name 123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "preventClose", "prevent_close", "true"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "categoryType", "category_type", "NEGATIVE"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", "turkey_group", UUID.randomUUID().toString()),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "preventClosing", "prevent_closing", "true"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "setting_value", "some setting value 123")
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

		when(deltaFacade.pushModification(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
		verify(deltaFacade, times(1)).pushModification(ArgumentMatchers.refEq(modifiedDelta));
	}

	private static Stream<Arguments> provideIncorrectValues() {
		// this doesn't throw exception
		// Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "12345")
		String uuid = UUID.randomUUID().toString();
		return Stream.of(
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "requiredUsageMs", "12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "requiredUsageMs", "12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "abc123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "-12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "123.45"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "invalid", "12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", "abc123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", "-123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", "123.45"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "conditionalGroup", "abc 123 $"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "name", "invalid group name 123 $"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "preventClose", "invalid"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "categoryType", "INVALID"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", "not-an-uuid"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", uuid + "q"), // 1 extra character
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", uuid.substring(1)), // 1 missing character at start
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", uuid.substring(0, uuid.length()-1)), // 1 missing character at end
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", uuid.replace(uuid.charAt(3), '$')), // 1 wrong character
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", uuid.replace('-', 'a')), // wrong slashes
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "preventClosing", "invalid"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "invalid setting value 123 %")
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
		verifyNoInteractions(deltaFacade);
	}

}
