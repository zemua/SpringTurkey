package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import devs.mrp.springturkey.Exceptions.WrongDataException;
import devs.mrp.springturkey.database.entity.enumerable.ActivityPlatform;
import devs.mrp.springturkey.database.entity.enumerable.CategoryType;
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.entity.enumerable.PlatformType;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.entity.ActivityCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.ConditionCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
import devs.mrp.springturkey.delta.validation.entity.SettingCreationDelta;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CreationDataConstrainer.class})
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
		ObjectMapper objectMapper = new ObjectMapper();
		return Stream.of(
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, "object", objectMapper.writeValueAsString(validGroup().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, "object", objectMapper.writeValueAsString(validActivity().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, "object", objectMapper.writeValueAsString(validSetting().build()))
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void testSucess(DeltaType deltaType, DeltaTable table, String fieldName, String jsonValue) throws JsonProcessingException, WrongDataException {
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(deltaType)
				.table(table)
				.recordId(UUID.randomUUID())
				.fieldName(fieldName)
				.textValue(jsonValue)
				.build();

		when(deltaFacade.pushCreation(ArgumentMatchers.refEq(delta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);
	}

	private static Stream<Arguments> provideIncorrectValues() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "object", objectMapper.writeValueAsString(validGroup().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, "invalid", objectMapper.writeValueAsString(validGroup().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, "object", objectMapper.writeValueAsString(validGroup().name("invalid 123 $").build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, "object", objectMapper.writeValueAsString(validGroup().type(null).build())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "object", objectMapper.writeValueAsString(validActivity().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, "invalid", objectMapper.writeValueAsString(validActivity().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, "object", objectMapper.writeValueAsString(validActivity().activityName("invalid 123 $").build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, "object", objectMapper.writeValueAsString(validActivity().activityType(null).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.ACTIVITY, "object", objectMapper.writeValueAsString(validActivity().categoryType(null).build())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "invalid", objectMapper.writeValueAsString(validCondition().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().conditionalGroup(null).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().targetGroup(null).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().requiredUsageMs(25000L).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().requiredUsageMs(-5L).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().lastDaysToConsider(35).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.CONDITION, "object", objectMapper.writeValueAsString(validCondition().lastDaysToConsider(-5).build())),
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.SETTING, "object", objectMapper.writeValueAsString(validSetting().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, "invalid", objectMapper.writeValueAsString(validSetting().build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, "object", objectMapper.writeValueAsString(validSetting().platformType(null).build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, "object", objectMapper.writeValueAsString(validSetting().settingKey("invalid 123 $").build())),
				Arguments.of(DeltaType.CREATION, DeltaTable.SETTING, "object", objectMapper.writeValueAsString(validSetting().settingValue("invalid 456 &").build()))
				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void testInvalidDeltaData(DeltaType deltaType, DeltaTable table, String fieldName, String jsonValue) throws JsonProcessingException, WrongDataException {
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(deltaType)
				.table(table)
				.recordId(UUID.randomUUID())
				.fieldName(fieldName)
				.textValue(jsonValue)
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
				.requiredUsageMs(120000L)
				.lastDaysToConsider(10);
	}

	private static SettingCreationDelta.SettingCreationDeltaBuilder validSetting() {
		return SettingCreationDelta.builder()
				.platformType(PlatformType.DESKTOP)
				.settingKey("some setting key 123")
				.settingValue("some setting value 123");
	}

}
