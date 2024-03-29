package devs.mrp.springturkey.validation;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AlphaNumericSpaceValidator implements ConstraintValidator<AlphaNumericSpaceConstraint, String> {

	@Override
	public void initialize(AlphaNumericSpaceConstraint contactNumber) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return StringUtils.isAlphanumericSpace(value);
	}

}
