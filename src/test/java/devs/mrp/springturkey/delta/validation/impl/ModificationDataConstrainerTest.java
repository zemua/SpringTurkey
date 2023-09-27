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

	/* TODO implement pass tests
	 * modify random check activedays
	 * modify random check negative questions
	 * modify random check positive questions
	 */

	/* TODO implement failing tests
	 * modify random question name
	 * modify random question question
	 * modify random question frequency
	 * modify random question multiplier
	 * modify random check name
	 * modify random check start active
	 * modify random check end active
	 * modify random check min check lapse
	 * modify random check max check lapse
	 * modify random check reward
	 * modify random check activedays
	 * modify random check negative questions
	 * modify random check positive questions
	 */

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("modificationConstraints")
	private DataConstrainer dataConstrainer;

	private static Stream<Arguments> provideCorrectValues() {
		String uuid = UUID.randomUUID().toString();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "requiredUsageMs", "12345"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", "lastDaysToConsider", "3"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "conditionalGroup", "conditionalGroup", uuid),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "name", "name", "some group name 123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "preventClose", "preventClose", "true"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "categoryType", "categoryType", "NEGATIVE"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", "group", UUID.randomUUID().toString()),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "preventClose", "preventClosing", "true"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "settingValue", "some setting value 123"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "name", "name", "updated name"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "question", "question", "updated question"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "frequency", "frequency", "3"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "multiplier", "multiplier", "2"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "name", "name", "modified name"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "startActive", "startActive", "11:22"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "endActive", "endActive", "11:22"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "minCheckLapse", "minCheckLapse", "11:22"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "maxCheckLapse", "maxCheckLapse", "11:22"),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "reward", "reward", "11:22")
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
				.jsonValue(textValue)
				.build();

		Delta modifiedDelta = delta.withFieldName(columnName);

		when(deltaFacade.pushModification(ArgumentMatchers.refEq(modifiedDelta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta).block();
		assertEquals(1, result);
		verify(deltaFacade, times(1)).pushModification(ArgumentMatchers.refEq(modifiedDelta));
	}

	private static Stream<Arguments> provideIncorrectValues() {
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
				.jsonValue(textValue)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
		verifyNoInteractions(deltaFacade);
	}

}
