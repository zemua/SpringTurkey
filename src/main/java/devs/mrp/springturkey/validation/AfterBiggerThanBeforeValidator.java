package devs.mrp.springturkey.validation;

import java.time.LocalTime;
import java.util.Objects;

import devs.mrp.springturkey.validation.dtodef.MinMax;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AfterBiggerThanBeforeValidator implements ConstraintValidator<MaxBiggerThanMinConstraint, MinMax<LocalTime>> {

	@Override
	public boolean isValid(MinMax<LocalTime> value, ConstraintValidatorContext context) {
		return !Objects.isNull(value.maxConstraint()) && !Objects.isNull(value.minConstraint()) && value.maxConstraint().compareTo(value.minConstraint()) >= 0;
	}

}
