package devs.mrp.springturkey.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = AlphaNumericSpaceValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AlphaNumericSpaceConstraint {
	String message() default "Value should contain only letters, numbers and spaces";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
