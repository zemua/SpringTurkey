package devs.mrp.springturkey.validation;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlphaNumericSpaceValidator implements ConstraintValidator<AlphaNumericSpaceConstraint, String> {

	@Override
	public void initialize(AlphaNumericSpaceConstraint contactNumber) {
		// intentionally left empty
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		log.debug("Validating {}", value);
		return !StringUtils.isBlank(value) && StringUtils.isAlphanumericSpace(value);
	}

}
