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
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
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
				Arguments.of(DeltaType.DELETION, DeltaTable.GROUP, "object", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.ACTIVITY, "object", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.CONDITION, "object", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, "object", "deletion")
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void testSucess(DeltaType deltaType, DeltaTable table, String fieldName, String textValue) throws JsonProcessingException, WrongDataException {
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(deltaType)
				.table(table)
				.recordId(UUID.randomUUID())
				.fieldName(fieldName)
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
				Arguments.of(DeltaType.MODIFICATION, DeltaTable.GROUP, "object", "deletion"),
				Arguments.of(DeltaType.CREATION, DeltaTable.GROUP, "object", "deletion"),

				Arguments.of(DeltaType.DELETION, DeltaTable.GROUP, "othervalue", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.ACTIVITY, "othervalue", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.CONDITION, "othervalue", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, "othervalue", "deletion"),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, null, "deletion"),

				Arguments.of(DeltaType.DELETION, DeltaTable.GROUP, "object", "othervalue"),
				Arguments.of(DeltaType.DELETION, DeltaTable.ACTIVITY, "object", "othervalue"),
				Arguments.of(DeltaType.DELETION, DeltaTable.CONDITION, "object", "othervalue"),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, "object", "othervalue"),
				Arguments.of(DeltaType.DELETION, DeltaTable.SETTING, "object", null)
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
				.jsonValue(jsonValue)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
		verifyNoInteractions(deltaFacade);
	}

	// TODO implement random checks and random blocks

}
