package prj.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import prj.model.User;
import prj.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppUserDetailsServiceTest {
    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    public void loadUserByUsernameTest() {
        User user = new User();
        user.setEmail("test@kcl.ac.uk");

        Mockito.when(userRepository.findByEmail("test@kcl.ac.uk")).thenReturn(user);

        UserDetails loadedUserDetails = appUserDetailsService.loadUserByUsername("test@kcl.ac.uk");

        assertEquals(loadedUserDetails.getUsername(), user.getEmail());
    }

    @Test
    public void loadNullUserByUsernameTest() {
        Mockito.when(userRepository.findByEmail("test@kcl.ac.uk")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> appUserDetailsService.loadUserByUsername("test@kcl.ac.uk"));
    }
}