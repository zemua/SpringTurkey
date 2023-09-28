package devs.mrp.springturkey.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class NullableAlphaNumericSpaceValidatorTest {

	private Validator validator;

	@BeforeEach
	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testValid() {
		var validable = new Validable();
		validable.field = "something valid 123";
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testInvalid() {
		var validable = new Validable();
		validable.field = "something invalid 123 $";
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	@Test
	void testNull() {
		var validable = new Validable();
		validable.field = null;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	private static class Validable {
		@NullableAlphaNumericSpaceConstraint
		private String field;
	}

}
