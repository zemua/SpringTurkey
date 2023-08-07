package devs.mrp.springturkey.delta.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.Exceptions.TurkeySurpriseException;

class FieldValidatorTest {

	@Test
	void testValidation() {
		FieldValidator validator = FieldValidator.builder()
				.columnName("some name")
				.predicate(s -> Pattern.compile("^hello.+").matcher(s).matches())
				.build();

		assertTrue(validator.isValid("hello world!"));
		assertFalse(validator.isValid("bye world!"));

		validator = FieldValidator.builder()
				.columnName("some name")
				.predicate(s -> Pattern.compile("^hello").matcher(s).matches())
				.build();
		assertFalse(validator.isValid("hello world!"));
		assertFalse(validator.isValid("bye world!"));
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
