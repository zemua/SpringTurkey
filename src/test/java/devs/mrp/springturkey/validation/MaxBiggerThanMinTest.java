package devs.mrp.springturkey.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import devs.mrp.springturkey.validation.dtodef.MinMax;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;

class MaxBiggerThanMinTest {

	private Validator validator;

	@BeforeEach
	void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testValidMinMax() {
		Validable validable = new Validable(LocalTime.of(1, 15), LocalTime.of(2,11));
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testInvalidMinMax() {
		Validable validable = new Validable(LocalTime.of(1, 15), LocalTime.of(0, 45));
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertFalse(violations.isEmpty());
	}

	@Test
	void testEqualMinMax() {
		LocalTime time  = LocalTime.of(1, 15);
		Validable validable = new Validable(time, time);
		Set<ConstraintViolation<Validable>> violations = validator.validate(validable);
		assertTrue(violations.isEmpty());
	}

	@MaxBiggerThanMinConstraint
	@AllArgsConstructor
	private static class Validable implements MinMax<LocalTime> {
		private LocalTime minTime;
		private LocalTime maxTime;

		@Override
		public LocalTime minConstraint() {
			return minTime;
		}

		@Override
		public LocalTime maxConstraint() {
			return maxTime;
		}
	}

}
