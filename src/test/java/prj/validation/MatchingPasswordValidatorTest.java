package prj.validation;

import org.junit.jupiter.api.Test;
import prj.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class MatchingPasswordValidatorTest {

    @Test
    public void isValidTrueTest() {
        MatchingPasswordValidator matchingPasswordValidator = new MatchingPasswordValidator();

        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setMatchingPassword("password");
        assertTrue(matchingPasswordValidator.isValid(userDto, null));
    }

    @Test
    public void isValidFalseTest() {
        MatchingPasswordValidator matchingPasswordValidator = new MatchingPasswordValidator();

        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setMatchingPassword("pwd");
        assertFalse(matchingPasswordValidator.isValid(userDto, null));
    }

}