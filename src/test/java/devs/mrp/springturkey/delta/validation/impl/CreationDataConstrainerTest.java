package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
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
import devs.mrp.springturkey.database.entity.enumerable.GroupType;
import devs.mrp.springturkey.database.service.DeltaFacadeService;
import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import devs.mrp.springturkey.delta.validation.DataConstrainer;
import devs.mrp.springturkey.delta.validation.entity.GroupCreationDelta;
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

	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator val = factory.getValidator();
		when(validator.validate(ArgumentMatchers.any())).thenAnswer(a -> val.validate(a.getArgument(0)));
	}

	private static Stream<Arguments> provideCorrectValues() {
		return Stream.of(
				// TODO
				//Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "required_usage_ms", "12345"),
				);
	}

	@ParameterizedTest
	@MethodSource("provideCorrectValues")
	void testSucess(DeltaType deltaType, DeltaTable table, String fieldName, String textValue) throws JsonProcessingException, WrongDataException {
		GroupCreationDelta creationDelta = GroupCreationDelta.builder().id(UUID.randomUUID()).name("valid 123").type(GroupType.NEGATIVE).preventClose(false).build();
		String json = objectMapper.writeValueAsString(creationDelta);
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.GROUP)
				.recordId(UUID.randomUUID())
				.fieldName("object")
				.textValue(json)
				.build();

		when(deltaFacade.pushCreation(ArgumentMatchers.refEq(delta))).thenReturn(1);

		int result = dataConstrainer.pushDelta(delta);
		assertEquals(1, result);

		fail("Not yet implemented");
	}

	private static Stream<Arguments> provideIncorrectValues() {
		// this doesn't throw exception
		// Arguments.of(DeltaType.MODIFICATION, DeltaTable.CONDITION, "requiredUsageMs", "12345")
		return Stream.of(
				// TODO
				// Arguments.of(DeltaType.MODIFICATION, DeltaTable.ACTIVITY, "requiredUsageMs", "12345")
				);
	}

	@ParameterizedTest
	@MethodSource("provideIncorrectValues")
	void testInvalidDeltaData(DeltaType deltaType, DeltaTable table, String fieldName, String textValue) throws JsonProcessingException {
		GroupCreationDelta creationDelta = GroupCreationDelta.builder().id(UUID.randomUUID()).name("invalid 123 $%&").type(GroupType.NEGATIVE).preventClose(false).build();
		String json = objectMapper.writeValueAsString(creationDelta);
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.GROUP)
				.recordId(UUID.randomUUID())
				.fieldName("object")
				.textValue(json)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));

		fail("Not yet implemented");
	}

}
