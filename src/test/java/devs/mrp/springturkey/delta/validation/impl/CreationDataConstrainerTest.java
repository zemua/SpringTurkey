package devs.mrp.springturkey.delta.validation.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

	@Test
	void testSucess() throws JsonProcessingException {
		GroupCreationDelta creationDelta = GroupCreationDelta.builder().id(UUID.randomUUID()).name("valid 123").type(GroupType.NEGATIVE).preventClose(false).build();
		String json = objectMapper.writeValueAsString(creationDelta);
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.GROUP)
				.recordId(null)
				.fieldName("object")
				.textValue(json)
				.build();


		fail("Not yet implemented");
	}

	@Test
	void testInvalidDeltaData() throws JsonProcessingException {
		GroupCreationDelta creationDelta = GroupCreationDelta.builder().id(UUID.randomUUID()).name("invalid 123 $%&").type(GroupType.NEGATIVE).preventClose(false).build();
		String json = objectMapper.writeValueAsString(creationDelta);
		Delta delta = Delta.builder()
				.timestamp(LocalDateTime.now())
				.deltaType(DeltaType.CREATION)
				.table(DeltaTable.GROUP)
				.recordId(null)
				.fieldName("object")
				.textValue(json)
				.build();

		assertThrows(WrongDataException.class, () -> dataConstrainer.pushDelta(delta));
	}

	@Test
	void testIncorrectDeltaType() {
		fail("Not yet implemented");
	}

}
