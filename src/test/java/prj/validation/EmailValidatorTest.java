package prj.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    @Test
    public void isValidTrueTest() {
        EmailValidator emailValidator = new EmailValidator();

        assertTrue(emailValidator.isValid("test@kcl.ac.uk", null));
        assertTrue(emailValidator.isValid("test.test@kcl.ac.uk", null));
        assertTrue(emailValidator.isValid("test-test@kcl.ac.uk", null));
    }

    @Test
    public void isValidFalseTest() {
        EmailValidator emailValidator = new EmailValidator();

        assertFalse(emailValidator.isValid("test@ac.uk", null));
        assertFalse(emailValidator.isValid("@kcl.ac.uk", null));
        assertFalse(emailValidator.isValid("test@mail.com", null));
    }

}