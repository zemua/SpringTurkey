package devs.mrp.springturkey.validation;

import java.time.LocalTime;
import java.util.Objects;

import devs.mrp.springturkey.validation.dtodef.MinMax;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableBeforeAndAfterValidator implements ConstraintValidator<NullableBeforeAndAfterConstraint, MinMax<LocalTime>> {

	@Override
	public boolean isValid(MinMax<LocalTime> value, ConstraintValidatorContext context) {
		return Objects.isNull(value.minConstraint()) || Objects.isNull(value.maxConstraint()) || value.maxConstraint().compareTo(value.minConstraint()) >= 0;
	}

}
