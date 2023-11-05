package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.exceptions.WrongDataException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DeletionDataConstrainer.class})
class DeletionDataConstrainerTest {

	@MockBean
	Validator validator;

	@MockBean
	private DeltaFacadeService deltaFacade;

	@Autowired
	@Qualifier("deletionConstraints")
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
				Arguments.of(DeltaType.DELETION, DeltaTable.GROUP, fieldOf("deletion", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.ACTIVITY, fieldOf("deletion", "TRue")),
				Arguments.of(DeltaType.DELETION, DeltaTable.CONDITION, fieldOf("deletion", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf("deletion", "true")),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf("deletion", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_QUESTION, fieldOf("deletion", "TRUE")),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_CHECK, fieldOf("deletion", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_CHECK, fieldOf("deletion", "true"))
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void testSucess(DeltaType deltaType, DeltaTable table, Map<String, Object> textValue) throws JsonProcessingException, WrongDataException {
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(deltaType)
				.table(table)
				.recordId(UUID.randomUUID())
				.jsonValue(textValue)
				.build();

		when(deltaFacade.pushDeletion(ArgumentMatchers.refEq(delta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta).block();
		assertEquals(1, result);
		verify(deltaFacade, times(1)).pushDeletion(ArgumentMatchers.refEq(delta));
	}

	private static Stream<Arguments> provideIncorrectValues() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return Stream.of(
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, fieldOf("deletion", true)),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, fieldOf("deletion", "true")),

				Arguments.of(DeltaType.DELETION, DeltaTable.GROUP, fieldOf("otherValue", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.ACTIVITY, fieldOf("otherValue", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.CONDITION, fieldOf("otherValue", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf("otherValue", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf(null, true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_QUESTION, fieldOf("otherValue", true)),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_CHECK, fieldOf("otherValue", true)),

				Arguments.of(DeltaType.DELETION, DeltaTable.GROUP, fieldOf("deletion", "false")),
				Arguments.of(DeltaType.DELETION, DeltaTable.ACTIVITY, fieldOf("deletion", "invalid")),
				Arguments.of(DeltaType.DELETION, DeltaTable.CONDITION, fieldOf("deletion", "true ")),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf("deletion", "invalid")),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf("deletion", "truee")),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, fieldOf("deletion", null)),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_QUESTION, fieldOf("deletion", "invalid")),
				Arguments.of(DeltaType.DELETION, DeltaTable.RANDOM_CHECK, fieldOf("deletion", "invalid"))
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

	private static Map<String, Object> fieldOf(String key, Object value) {
		Map<String,Object> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

}
