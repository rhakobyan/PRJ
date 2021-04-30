package prj.dto;

import prj.validation.UniqueEmail;
import prj.validation.ValidEmail;
import prj.validation.ValidMatchingPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/*
 * The User Data Transfer Object Class (DTO) class is used for transferring user related data for registration and login.
 * This class contains only the necessary fields for registration, saving the trouble of sending back and forth
 * the whole user class with much more data, unnecessary for the registration purposes.
 *
 * Each field is marked with validation annotations. When a DTO object is created, Spring checks that these
 * this validations pass.
 * Some of the validation here are custom-written, and some were already present in Spring.
 */
@ValidMatchingPassword
public class UserDto {
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @ValidEmail
    @UniqueEmail
    private String email;

    /*
     * Getters and Setters
     */

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}