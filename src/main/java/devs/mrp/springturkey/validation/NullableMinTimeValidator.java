package devs.mrp.springturkey.validation;

import java.time.LocalTime;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableMinTimeValidator implements ConstraintValidator<NullableMinTimeConstraint, LocalTime> {

	private int hours;
	private int minutes;

	@Override
	public void initialize(NullableMinTimeConstraint minTime) {
		this.hours = minTime.hours();
		this.minutes = minTime.minutes();
	}

	@Override
	public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
		LocalTime minTime = LocalTime.of(hours, minutes);
		return Objects.isNull(value) || value.compareTo(minTime) >= 0;
	}

}
