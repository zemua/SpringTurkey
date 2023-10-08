package devs.mrp.springturkey.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = NullableMinDurationValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableMinDurationConstraint {
	int hours() default 0;
	int minutes() default 0;
	String message() default "The value should be higher than the minimum";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
