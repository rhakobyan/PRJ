package prj.service;

import prj.repository.UserRepository;
import prj.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import prj.userdetails.AppUserDetails;

/*
 * AppUserDetailsService class provides concrete implementation of UserDetailsService.
 * Specifically, it overrides the loadUserByUsername. As there are no usernames used in this system, it
 * instead tells to use the user email.
 */
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /*
     * This method overrides the loadUserByUsername method.
     * As there are no usernames in this system, it tells the system to use emails instead.
     * @param username The username (in this system email) of the user
     * @return the user withe @param username email.
     * @throws UsernameNotFoundException if no user with the specified email was found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new AppUserDetails(user);
    }
}
