package devs.mrp.springturkey.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = AfterBiggerThanBeforeValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxBiggerThanMinConstraint {
	String message() default "Max value should be bigger than min value";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
