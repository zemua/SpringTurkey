package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Duration;
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

import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.service.DeltaServiceFacade;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataPushConstrainer;
import devs.mrp.springturkey.exceptions.WrongDataException;
import devs.mrp.springturkey.utils.impl.ObjectMapperProvider;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModificationDeltaFilterService.class, ObjectMapperProvider.class})
class ModificationDeltaFilterServiceTest {

	@MockBean
	private DeltaServiceFacade deltaFacade;

	@Autowired
	@Qualifier("modificationConstraints")
	private DataPushConstrainer dataConstrainer;

	private static Stream<Arguments> provideCorrectValues() {
		String uuid = UUID.randomUUID().toString();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("requiredUsageMs", Duration.ofMinutes(1))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("lastDaysToConsider", 3)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("conditionalGroup", uuid)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("conditionalGroup", uuid.toString())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("name", "some group name 123")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("preventClose", true)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("preventClose", "true")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("categoryType", CategoryType.NEGATIVE)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("categoryType", "NEGATIVE")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("group", UUID.randomUUID())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("preventClosing", true)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, fieldOf("settingValue", "some setting value 123")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("name", "updated name")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("question", "updated question")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("frequency", 3)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("multiplier", 2)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("name", "modified name")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("startActive", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("endActive", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("minCheckLapse", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("maxCheckLapse", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("reward", LocalTime.of(11, 22))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("activeDays", Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("negativeQuestions", Set.of(UUID.randomUUID(), UUID.randomUUID()))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("positiveQuestions", Set.of(UUID.randomUUID(), UUID.randomUUID())))
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void pushesCorrectly(DeltaType deltaType, DeltaTable table, Map<String,Object> jsonValue) throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(deltaType)
				.table(table)
				.recordId(id)
				.jsonValue(jsonValue)
				.build();

		when(deltaFacade.pushModification(ArgumentMatchers.refEq(delta))).thenReturn(Mono.just(1));

		int result = dataConstrainer.pushDelta(delta).block();
		assertEquals(1, result);
		verify(deltaFacade, times(1)).pushModification(ArgumentMatchers.refEq(delta));
	}

	private static Stream<Arguments> provideIncorrectValues() {
		String uuid = UUID.randomUUID().toString();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("nonExistingField", "0")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("nonExistingField", "0")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("nonExistingField", "0")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("nonExistingField", "0")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("nonExistingField", "0")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("requiredUsageMs", "abcdef")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("lastDaysToConsider", -3)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, fieldOf("conditionalGroup", uuid.toString() + "a")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("name", "some group name 123 $$$")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("preventClose", "wrong")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("categoryType", "wrong")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("group", UUID.randomUUID().toString()+"a")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, fieldOf("preventClosing", "wrong")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, fieldOf("settingValue", "some setting value 123 $%")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("name", "updated name $%")),
				// Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, "question", fieldOf("question", 123)), // any object that can be converted to string is valid
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("frequency", -1)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, fieldOf("multiplier", -1)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("name", "modified name $%")),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("startActive", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("endActive", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("minCheckLapse", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("maxCheckLapse", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("reward", 123)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("activeDays", Set.of("wrong"))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("negativeQuestions", Set.of(UUID.randomUUID().toString()+"a", UUID.randomUUID()))),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, fieldOf("positiveQuestions", Set.of(UUID.randomUUID().toString()+"a", UUID.randomUUID())))
				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void pushesFails(DeltaType deltaType, DeltaTable table, Map<String,Object> jsonValue) throws WrongDataException {
		UUID id = UUID.randomUUID();
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.of(2023, 2, 14, 4, 25))
				.deltaType(deltaType)
				.table(table)
				.recordId(id)
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
