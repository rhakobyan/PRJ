package prj.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    public void settersAndGettersTest() {
        UserDto userDto = new UserDto();

        userDto.setEmail("test@kcl.ac.uk");
        userDto.setFirstName("test");
        userDto.setLastName("test");
        userDto.setPassword("password");
        userDto.setMatchingPassword("password");

        assertEquals(userDto.getEmail(), "test@kcl.ac.uk");
        assertEquals(userDto.getFirstName(), "test");
        assertEquals(userDto.getLastName(), "test");
        assertEquals(userDto.getPassword(), "password");
        assertEquals(userDto.getMatchingPassword(), "password");
    }

}