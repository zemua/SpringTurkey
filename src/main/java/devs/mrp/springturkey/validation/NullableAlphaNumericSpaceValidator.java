package devs.mrp.springturkey.validation;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableAlphaNumericSpaceValidator implements ConstraintValidator<NullableAlphaNumericSpaceConstraint, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Objects.isNull(value) || (!StringUtils.isBlank(value) && StringUtils.isAlphanumericSpace(value));
	}

}
