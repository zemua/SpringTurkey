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

class AlphaNumericSpaceValidatorTest {

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
		Set<ConstraintViolation<AlphaNumericSpaceValidatorTest.Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testInvalid() {
		var validable = new Validable();
		validable.field = "something invalid 123 $";
		Set<ConstraintViolation<AlphaNumericSpaceValidatorTest.Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	private static class Validable {
		@AlphaNumericSpaceConstraint
		private String field;
	}

}