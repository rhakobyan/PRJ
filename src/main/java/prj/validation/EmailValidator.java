package prj.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * EmailValidator class specifies the logic for validating the email address presented by the user.
 * This class implements the ConstraintValidator interface and overrides its isValid method
 * where the logic for validating the email address is written.
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    /*
     * The isValid method checks if the presented email is a valid KCL email address.
     * It does this by matching the user presented email address against a regular expression.
     * @param email The email field in the registration, obtained from a UserDto object.
     * @return Whether the @param email matches the present regular expression.
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@kcl[.]ac[.]uk$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
