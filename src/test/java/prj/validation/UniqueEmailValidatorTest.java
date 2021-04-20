package prj.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import prj.model.User;
import prj.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UniqueEmailValidatorTest {
    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private UniqueEmailValidator uniqueEmailValidator;

    @Test
    public void isValidTrueTest() {
        Mockito.when(userRepository.findByEmail("test@kcl.ac.uk")).thenReturn(null);

        assertTrue(uniqueEmailValidator.isValid("test@kcl.ac.uk", null));
    }

    @Test
    public void isValidFalseTest() {
        Mockito.when(userRepository.findByEmail("test@kcl.ac.uk")).thenReturn(new User());

        assertFalse(uniqueEmailValidator.isValid("test@kcl.ac.uk", null));
    }

}