package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.model.user.UserRole;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @Test
    void loadUserByUsername_isGoodTest() {
        String username = "username";
        String passwordHash = "passwordHash";

        User user = User.builder().username(username).passwordHash(passwordHash).role(UserRole.ROLE_USER).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails springUser = myUserDetailsService.loadUserByUsername(username);

        assertEquals(user.getUsername(),springUser.getUsername());
        assertEquals(user.getPasswordHash(), springUser.getPassword());
        assertEquals(new SimpleGrantedAuthority(user.getRole().name()), springUser.getAuthorities().iterator().next());
    }

    @Test
    void loadUserByEmail_shouldThrowUsernameNotFoundException_whenUsernameIncorrected() {
        String username = "wrongUsername";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> myUserDetailsService.loadUserByUsername(username));
    }

}
