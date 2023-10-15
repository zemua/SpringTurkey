package devs.mrp.springturkey.validation;

import java.time.Duration;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableMinDurationValidator implements ConstraintValidator<NullableMinDurationConstraint, Duration> {

	private int hours;
	private int minutes;

	@Override
	public void initialize(NullableMinDurationConstraint minTime) {
		this.hours = minTime.hours();
		this.minutes = minTime.minutes();
	}

	@Override
	public boolean isValid(Duration value, ConstraintValidatorContext context) {
		Duration minTime = Duration.ofHours(hours).plusMinutes(minutes);
		return Objects.isNull(value) || value.compareTo(minTime) >= 0;
	}

}