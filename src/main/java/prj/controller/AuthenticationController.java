package prj.controller;

import prj.repository.UserRepository;
import prj.dto.UserDto;

import prj.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/*
 * The Authentication Controller handles the requests for registration and logging in to the system.
 */
@Controller
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    /*
     * Handle GET requests at the "/register" URL for displaying the user registration from.
     * Create an empty UserDto object and pass it on to the view.
     * This DTO object will be used by the view for filling in the fields of the registration form
     * and passing them back to the controller.
     * @param model A Model object to be passed on to the view.
     * @return The registration form view page.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "auth/register";
    }

    /*
     * Handle POST requests at the "/register" URL for registering the user into the system.
     * If the validations of @param userDto all passed, then a new user object is firstly created.
     * The password of the user is hashed and the user itself is stored in the database.
     * After successful registration the login page is returned.
     * If there are validation errors, then the registration form is returned back which will display the
     * relevant errors through bindingResults.
     * @param userDto A data transfer object for representing the user registration fields and validating them.
     * @param bindingResult An object for storing the results of the validation of the userDto.
     * @param model A Model object to be passed on to the view.
     * @return If registration has validation errors - back to the registration view, If registration successful - the login page
     */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {
        // If there are validation errors, return the registration view page
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // Otherwise, create a new user and hash the user's password
        User user = new User(userDto);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Save the newly created user in the database
        userRepository.save(user);

        // Redirect the user to login page
        return "redirect:/login";
    }

    /*
     * Handle GET requests at the "/login" URL for displaying the user login page.
     * @param model A Model object to be passed on to the view.
     * @return The login form view page.
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "auth/login";
    }
}
