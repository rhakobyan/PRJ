package prj.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import prj.dto.UserDto;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void registrationFrontPageTest() throws Exception {
        mvc.perform(get("/register")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void registrationSuccessTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("newTest@kcl.ac.uk");
        userDto.setPassword("Password");
        userDto.setMatchingPassword("Password");
        userDto.setFirstName("New");
        userDto.setLastName("Test");

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .flashAttr("user", userDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

    }

    @Test
    public void registrationEmptyUserDetailsTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("");
        userDto.setPassword("");
        userDto.setMatchingPassword("");
        userDto.setFirstName("");
        userDto.setLastName("");

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .flashAttr("user", userDto))
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors("user"))
                .andExpect(model().attributeHasFieldErrors("user", "email", "password", "firstName", "firstName"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "ValidEmail"))
                .andExpect(model().attributeHasFieldErrorCode("user", "firstName", "NotEmpty"))
                .andExpect(model().attributeHasFieldErrorCode("user", "lastName", "NotEmpty"))
                .andExpect(model().attributeHasFieldErrorCode("user", "password", "NotEmpty"));
    }

    @Test
    public void registrationWrongEmailFormatTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("newTest@kcl.uk");
        userDto.setPassword("Password");
        userDto.setMatchingPassword("Password");
        userDto.setFirstName("New");
        userDto.setLastName("Test");

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .flashAttr("user", userDto))
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors("user"))
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "ValidEmail"));
    }

    @Test
    public void registrationNonMatchingPasswordsTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("newTest@kcl.ac.uk");
        userDto.setPassword("Password");
        userDto.setMatchingPassword("OtherPassword");
        userDto.setFirstName("New");
        userDto.setLastName("Test");

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .flashAttr("user", userDto))
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors("user"))
                .andExpect(model().errorCount(1));
    }

    @Test
    public void registrationUsedEmailTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@kcl.ac.uk");
        userDto.setPassword("Password");
        userDto.setMatchingPassword("Password");
        userDto.setFirstName("New");
        userDto.setLastName("Test");

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .flashAttr("user", userDto))
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors("user"))
                .andExpect(model().attributeHasFieldErrors("user", "email"))
                .andExpect(model().attributeHasFieldErrorCode("user", "email", "UniqueEmail"));
    }

    @Test
    public void logInPageTest() throws Exception {
        mvc.perform(get("/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(0));
    }
}
