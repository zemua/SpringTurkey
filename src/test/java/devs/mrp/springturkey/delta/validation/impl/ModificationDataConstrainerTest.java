package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
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
		String uuid = UUID.randomUUID().toString();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "requiredUsageMs", fieldOf("requiredUsageMs", LocalTime.of(0, 0))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", "lastDaysToConsider", fieldOf("lastDaysToConsider", 3)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "conditionalGroup", "conditionalGroup", fieldOf("conditionalGroup", uuid)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "conditionalGroup", "conditionalGroup", fieldOf("conditionalGroup", uuid.toString())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "name", "name", fieldOf("name", "some group name 123")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "preventClose", "preventClose", fieldOf("preventClose", true)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "preventClose", "preventClose", fieldOf("preventClose", "true")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "categoryType", "categoryType", fieldOf("categoryType", CategoryType.NEGATIVE)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "categoryType", "categoryType", fieldOf("categoryType", "NEGATIVE")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", "group", fieldOf("group", UUID.randomUUID())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "preventClose", "preventClosing", fieldOf("preventClosing", true)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", "settingValue", fieldOf("settingValue", "some setting value 123")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "name", "name", fieldOf("name", "updated name")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "question", "question", fieldOf("question", "updated question")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "frequency", "frequency", fieldOf("frequency", 3)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "multiplier", "multiplier", fieldOf("multiplier", 2)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "name", "name", fieldOf("name", "modified name")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "startActive", "startActive", fieldOf("startActive", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "endActive", "endActive", fieldOf("endActive", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "minCheckLapse", "minCheckLapse", fieldOf("minCheckLapse", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "maxCheckLapse", "maxCheckLapse", fieldOf("maxCheckLapse", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "reward", "reward", fieldOf("reward", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "activeDays", "activeDays", fieldOf("activeDays", Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "negativeQuestions", "negativeQuestions", fieldOf("negativeQuestions", Set.of(UUID.randomUUID(), UUID.randomUUID()))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "positiveQuestions", "positiveQuestions", fieldOf("positiveQuestions", Set.of(UUID.randomUUID(), UUID.randomUUID())))
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void pushesCorrectly(DeltaType deltaType, DeltaTable table, String fieldName, String columnName, Map<String,Object> jsonValue) throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(deltaType)
				.table(table)
				.recordId(id)
				.fieldName(fieldName)
				.jsonValue(jsonValue)
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
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", fieldOf("requiredUsageMs", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "lastDaysToConsider", fieldOf("lastDaysToConsider", -3)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "conditionalGroup", fieldOf("conditionalGroup", uuid.toString() + "a")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "name", fieldOf("name", "some group name 123 $$$")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "preventClose", fieldOf("preventClose", "wrong")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "categoryType", fieldOf("categoryType", "wrong")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "groupId", fieldOf("group", UUID.randomUUID().toString()+"a")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "preventClose", fieldOf("preventClosing", "wrong")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "settingValue", fieldOf("settingValue", "some setting value 123 $%")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "name", fieldOf("name", "updated name $%")),
				// Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "question", fieldOf("question", 123)), // any object that can be converted to string is valid
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "frequency", fieldOf("frequency", -1)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "multiplier", fieldOf("multiplier", -1)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "name", fieldOf("name", "modified name $%")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "startActive", fieldOf("startActive", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "endActive", fieldOf("endActive", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "minCheckLapse", fieldOf("minCheckLapse", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "maxCheckLapse", fieldOf("maxCheckLapse", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "reward", fieldOf("reward", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "activeDays", fieldOf("activeDays", Set.of("wrong"))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "negativeQuestions", fieldOf("negativeQuestions", Set.of(UUID.randomUUID().toString()+"a", UUID.randomUUID()))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, "positiveQuestions", fieldOf("positiveQuestions", Set.of(UUID.randomUUID().toString()+"a", UUID.randomUUID())))
				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void pushesFails(DeltaType deltaType, DeltaTable table, String fieldName, Map<String,Object> jsonValue) throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(deltaType)
				.table(table)
				.recordId(id)
				.fieldName(fieldName)
				.jsonValue(jsonValue)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
		verifyNoInteractions(deltaFacade);
	}

	private static Map<String, Object> fieldOf(String key, Object value) {
		Map<String,Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

}
