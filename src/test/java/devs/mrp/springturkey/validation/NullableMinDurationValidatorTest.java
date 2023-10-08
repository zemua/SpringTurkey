package devs.mrp.springturkey.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class NullableMinDurationValidatorTest {

	private Validator validator;

	@BeforeEach
	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testValidMinutes() {
		var validable = new Validable();
		validable.minTime = Duration.ofHours(1).plusMinutes(15);
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testValidHours() {
		var validable = new Validable();
		validable.minTime = Duration.ofHours(2).plusMinutes(1);
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testInvalidMinutes() {
		var validable = new Validable();
		validable.minTime = Duration.ofHours(1).plusMinutes(1);
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	@Test
	void testInvalidHours() {
		var validable = new Validable();
		validable.minTime = Duration.ofHours(0).plusMinutes(15);
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	@Test
	void testEqualTime() {
		var validable = new Validable();
		validable.minTime = Duration.ofHours(1).plusMinutes(5);
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testNull() {
		var validable = new Validable();
		validable.minTime = null;
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	private static class Validable {
		@NullableMinDurationConstraint(minutes = 5, hours = 1)
		private Duration minTime;
	}

}
