package prj.userdetails;

import org.junit.jupiter.api.Test;
import prj.model.User;

import static org.junit.jupiter.api.Assertions.*;

class AppUserDetailsTest {

    @Test
    public void gettersTest() {
        User user = new User();

        user.setEmail("test@kcl.ac.uk");
        user.setPassword("password");

        AppUserDetails appUserDetails = new AppUserDetails(user);

        assertEquals(appUserDetails.getUser(), user);
        assertEquals(appUserDetails.getPassword(), "password");
        assertEquals(appUserDetails.getUsername(), "test@kcl.ac.uk");
        assertNull(appUserDetails.getAuthorities());
        assertTrue(appUserDetails.isAccountNonExpired());
        assertTrue(appUserDetails.isAccountNonLocked());
        assertTrue(appUserDetails.isCredentialsNonExpired());
        assertTrue(appUserDetails.isEnabled());
    }

}