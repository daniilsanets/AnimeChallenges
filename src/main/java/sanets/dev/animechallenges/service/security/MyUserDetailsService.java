package sanets.dev.animechallenges.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.repository.UserRepository;
import sanets.dev.animechallenges.security.UserPrincipal;

import java.util.List;

import static sanets.dev.animechallenges.exception.ErrorMessages.USER_NOT_FOUND_MSG;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String message = USER_NOT_FOUND_MSG + username;

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(message));

        return new UserPrincipal(
                user.getUid(),
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }

}
