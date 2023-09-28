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

class NullableMinMaxValidatorTest {

	private Validator validator;

	@BeforeEach
	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testValid() {
		var validable = new Validable();
		validable.value = 7;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testTooSmall() {
		var validable = new Validable();
		validable.value = 4;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	@Test
	void testTooBig() {
		var validable = new Validable();
		validable.value = 11;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	@Test
	void testNull() {
		var validable = new Validable();
		validable.value = null;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testSmallest() {
		var validable = new Validable();
		validable.value = 5;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testBiggest() {
		var validable = new Validable();
		validable.value = 10;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	private static class Validable {
		@NullableMinMaxConstraint(min = 5, max = 10)
		private Integer value;
	}

}
