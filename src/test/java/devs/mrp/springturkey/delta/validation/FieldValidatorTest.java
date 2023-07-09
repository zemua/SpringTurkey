package devs.mrp.springturkey.delta.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.delta.validation.FieldValidator;

class FieldValidatorTest {

	@Test
	void testValidation() {
		FieldValidator validator = FieldValidator.builder().fieldName("some name").pattern("^hello.+").build();
		assertTrue(validator.isValid("hello world!"));
		assertFalse(validator.isValid("bye world!"));
		validator = FieldValidator.builder().fieldName("some name").pattern("^hello").build();
		assertFalse(validator.isValid("hello world!"));
		assertFalse(validator.isValid("bye world!"));
	}

	@Test
	void testNotNullFields() {
		assertThrows(Exception.class, () -> FieldValidator.builder().fieldName(null).pattern("^hello").build());
		assertThrows(Exception.class, () -> FieldValidator.builder().fieldName("some name").pattern(null).build());
		assertThrows(Exception.class, () -> FieldValidator.builder().fieldName("some name").build());
		assertThrows(Exception.class, () -> FieldValidator.builder().pattern("^hello").build());
	}

}
