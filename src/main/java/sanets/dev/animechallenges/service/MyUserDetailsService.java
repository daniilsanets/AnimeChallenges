package sanets.dev.animechallenges.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG = "User not found";

    private UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String message = USER_NOT_FOUND_MSG + username;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(message));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }

}
