package devs.mrp.springturkey.delta.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;

class FieldValidatorTest {

	@Test
	void testModification() {
		FieldValidator validator = FieldValidator.builder()
				.columnName("some name")
				.modifiable(true)
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertTrue(validator.isValidModification("hello world!"));
		assertFalse(validator.isValidModification("bye world!"));

		validator = FieldValidator.builder()
				.columnName("some name")
				.modifiable(true)
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build();
		assertFalse(validator.isValidModification("hello world!"));
		assertFalse(validator.isValidModification("bye world!"));

		validator = FieldValidator.builder()
				.columnName("some name")
				.modifiable(false)
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertFalse(validator.isValidModification("hello world!"));
		assertFalse(validator.isValidModification("bye world!"));
	}

	@Test
	void testCreation() {
		FieldValidator validator = FieldValidator.builder()
				.columnName("some name")
				.creatable(true)
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertTrue(validator.isValidCreation("hello world!"));
		assertFalse(validator.isValidCreation("bye world!"));

		validator = FieldValidator.builder()
				.columnName("some name")
				.creatable(true)
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build();
		assertFalse(validator.isValidCreation("hello world!"));
		assertFalse(validator.isValidCreation("bye world!"));

		validator = FieldValidator.builder()
				.columnName("some name")
				.creatable(false)
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertFalse(validator.isValidCreation("hello world!"));
		assertFalse(validator.isValidCreation("bye world!"));
	}

	@Test
	void testNotNullFields() {
		assertThrows(TurkeySurpriseException.class, () -> FieldValidator.builder()
				.columnName(null)
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build());

		assertThrows(TurkeySurpriseException.class, () -> FieldValidator.builder()
				.columnName("some name")
				.predicate(null)
				.build());

		assertThrows(TurkeySurpriseException.class, () -> FieldValidator.builder()
				.columnName("some name")
				.build());

		assertThrows(TurkeySurpriseException.class, () -> FieldValidator.builder()
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build());
	}

}
