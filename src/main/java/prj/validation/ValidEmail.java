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
 * ValidEmail annotation is used for validating the email field of the registration.
 * The email should be structured as a valid email address and should additionally end with kcl.ac.uk
 * to ensure it is a King's College email address.
 * The Constraint annotation below point to the EmailValidator class where the Validation logic is written.
 */

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented

public @interface ValidEmail {

    // The message to return if the field is invalid.
    String message() default "Invalid KCL Email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
