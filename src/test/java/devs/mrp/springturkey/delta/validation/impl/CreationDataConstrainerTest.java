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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import devs.mrp.springturkey.database.entity.RandomQuestion;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.database.entity.enumerable.RandomBlockType;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.ConditionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.RandomCheckCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.RandomQuestionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;
import devs.mrp.springturkey.exceptions.WrongDataException;
import devs.mrp.springturkey.utils.impl.ObjectMapperProvider;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CreationDataConstrainer.class, ObjectMapperProvider.class})
class CreationDataConstrainerTest {

	@MockBean
	Validator validator;

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("creationConstraints")
	private DataConstrainer dataConstrainer;

	@BeforeEach
	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator val = factory.getValidator();
		when(validator.validate(ArgumentMatchers.any())).thenAnswer(a -> val.validate(a.getArgument(0)));
	}

	private static Stream<Arguments> provideCorrectValues() throws JsonProcessingException {
		ObjectMapper objectMapper = JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();
		return Stream.of(
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, objectMapper.convertValue(validGroup().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, objectMapper.convertValue(validActivity().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, objectMapper.convertValue(validSetting().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().build(), Map.class))
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void testSucess(DeltaType deltaType, DeltaTable table, Map<String,Object> jsonValue) throws JsonProcessingException, WrongDataException {
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(deltaType)
				.table(table)
				.recordId(UUID.randomUUID())
				.jsonValue(jsonValue)
				.build();

		when(deltaFacade.pushCreation(ArgumentMatchers.refEq(delta))).thenReturn(Mono.just(1));

		int result = dataConstrainer.pushDelta(delta).block();
		assertEquals(1, result);
		verify(deltaFacade, times(1)).pushCreation(ArgumentMatchers.refEq(delta));
	}

	private static Stream<Arguments> provideIncorrectValues() throws JsonProcessingException {
		ObjectMapper objectMapper = JsonMapper.builder()
				.addModule(new JavaTimeModule())
				.build();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, objectMapper.convertValue(validGroup().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, objectMapper.convertValue(validGroup().name("invalid 123 $").build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, objectMapper.convertValue(validGroup().type(null).build(), Map.class)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, objectMapper.convertValue(validActivity().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, objectMapper.convertValue(validActivity().activityName("invalid 123 $").build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, objectMapper.convertValue(validActivity().activityType(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, objectMapper.convertValue(validActivity().categoryType(null).build(), Map.class)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().conditionalGroup(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().targetGroup(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().requiredUsageMs(Duration.ofMillis(25000L)).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().requiredUsageMs(Duration.ofMillis(-5L)).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().lastDaysToConsider(35).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, objectMapper.convertValue(validCondition().lastDaysToConsider(-5).build(), Map.class)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, objectMapper.convertValue(validSetting().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, objectMapper.convertValue(validSetting().platformType(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, objectMapper.convertValue(validSetting().settingKey("invalid 123 $").build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, objectMapper.convertValue(validSetting().settingValue("invalid 456 &").build(), Map.class)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().type(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().name("invalid 123 $%").build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().frequency(0).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().frequency(-1).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().multiplier(0).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_QUESTION, objectMapper.convertValue(validQuestion().multiplier(-1).build(), Map.class)),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().name("invalid 123 $%").build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().startActive(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().endActive(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().minCheckLapse(LocalTime.of(0, 0)).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().maxCheckLapse(LocalTime.of(0, 0)).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().reward(LocalTime.of(0, 0)).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().activeDays(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().negativeQuestions(null).build(), Map.class)),
				Arguments.of(DeltaType.CREATION, DeltaTable.RANDOM_CHECK, objectMapper.convertValue(validCheck().positiveQuestions(null).build(), Map.class))
				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void testInvalidDeltaData(DeltaType deltaType, DeltaTable table, Map<String,Object> jsonValue) throws JsonProcessingException, WrongDataException {
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(deltaType)
				.table(table)
				.recordId(UUID.randomUUID())
				.jsonValue(jsonValue)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
		verifyNoInteractions(deltaFacade);
	}

	private static GroupCreationDelta.GroupCreationDeltaBuilder validGroup() {
		return GroupCreationDelta.builder()
				.name("valid 123")
				.type(GroupType.NEGATIVE)
				.preventClose(false);
	}

	private static ActivityCreationDelta.ActivityCreationDeltaBuilder validActivity() {
		return ActivityCreationDelta.builder()
				.activityName("valid 123")
				.activityType(ActivityPlatform.ANDROID_APP)
				.categoryType(CategoryType.NEGATIVE)
				.groupId(UUID.randomUUID())
				.preventClose(false);
	}

	private static ConditionCreationDelta.ConditionCreationDeltaBuilder validCondition() {
		return ConditionCreationDelta.builder()
				.conditionalGroup(UUID.randomUUID())
				.targetGroup(UUID.randomUUID())
				.requiredUsageMs(Duration.ofMillis(120000L))
				.lastDaysToConsider(10);
	}

	private static SettingCreationDelta.SettingCreationDeltaBuilder validSetting() {
		return SettingCreationDelta.builder()
				.platformType(PlatformType.DESKTOP)
				.settingKey("some setting key 123")
				.settingValue("some setting value 123");
	}

	private static RandomQuestionCreationDelta.RandomQuestionCreationDeltaBuilder validQuestion() {
		return RandomQuestionCreationDelta.builder()
				.type(RandomBlockType.POSITIVE)
				.name("some valid name")
				.question("some random question?")
				.frequency(1)
				.multiplier(1);
	}

	private static RandomQuestion.RandomQuestionBuilder dbQuestion() {
		return RandomQuestion.builder();
	}

	private static RandomCheckCreationDelta.RandomCheckCreationDeltaBuilder validCheck() {
		return RandomCheckCreationDelta.builder()
				.name("valid name")
				.startActive(LocalTime.of(0,0))
				.endActive(LocalTime.of(0, 0))
				.minCheckLapse(LocalTime.of(0,30))
				.maxCheckLapse(LocalTime.of(2, 0))
				.reward(LocalTime.of(1,0))
				.activeDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
				.negativeQuestions(Set.of(dbQuestion().type(RandomBlockType.NEGATIVE).build(), dbQuestion().type(RandomBlockType.NEGATIVE).name("second name").build()))
				.positiveQuestions(Set.of(dbQuestion().type(RandomBlockType.POSITIVE).build(), dbQuestion().type(RandomBlockType.POSITIVE).name("second name").build()));
	}

}
