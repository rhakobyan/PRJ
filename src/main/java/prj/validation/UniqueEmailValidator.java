package prj.validation;

import prj.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * UniqueEmailValidator class specifies the logic for validating the uniqueness of the
 * email address presented by the user.
 * This class implements the ConstraintValidator interface and overrides its isValid method
 * where the logic for validating the email address is written.
 */
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    /*
     * The isValid method checks if the presented email is unique or not.
     * It does this by querying the database to see if there is already a user with the specified email.
     * @param email The email field in the registration, obtained from a UserDto object.
     * @return Whether there is a user in the database with @param email.
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
       return userRepository.findByEmail(email) == null;
    }
}
