package devs.mrp.springturkey.validation;

import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableMinValidator implements ConstraintValidator<NullableMinConstraint, Integer> {

	private int min;

	@Override
	public void initialize(NullableMinConstraint min) {
		this.min = min.min();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return Objects.isNull(value) || value >= this.min;
	}

}
