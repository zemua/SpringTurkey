package devs.mrp.springturkey.validation;

import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableMinMaxValidator implements ConstraintValidator<NullableMinMaxConstraint, Integer> {

	private int min;
	private int max;

	@Override
	public void initialize(NullableMinMaxConstraint minMax) {
		this.min = minMax.min();
		this.max = minMax.max();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return Objects.isNull(value) || (value >= min && value <= max);
	}

}
