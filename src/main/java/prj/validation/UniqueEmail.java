package prj.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/*
 * UniqueEmail annotation is used for validating the email field of the registration.
 * This validation ensures that the email presented at login is not already taken.
 * The Constraint annotation below point to the UniqueEmailValidator class where the Validation logic is written.
 */

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented

public @interface UniqueEmail {

    // The message to return if the field is invalid.
    String message() default "This email is already registered";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
