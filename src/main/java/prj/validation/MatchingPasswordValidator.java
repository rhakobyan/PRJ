package prj.validation;

import prj.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * MatchingPasswordValidator class specifies the logic for validating
 * the password fields of the registration form.
 * This class implements the ConstraintValidator interface and overrides its isValid method
 * where the logic for validating the password fields is written.
 */
public class MatchingPasswordValidator implements ConstraintValidator<ValidMatchingPassword, Object> {

    @Override
    public void initialize(ValidMatchingPassword constraintAnnotation) {
    }

    /*
     * The isValid method check if the password fields of a UserDto object are equal to each other.
     * @param obj the UserDto object.
     * @return Whether the password fields of the @param obj are equal to each other.
     */
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto userDto = (UserDto) obj;
        return userDto.getPassword().equals(userDto.getMatchingPassword());
    }
}
