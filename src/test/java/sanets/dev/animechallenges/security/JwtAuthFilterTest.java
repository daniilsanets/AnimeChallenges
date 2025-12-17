package sanets.dev.animechallenges.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.service.MyUserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    MyUserDetailsService userDetailsService;

    @InjectMocks
    JwtAuthFilter jwtAuthFilter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Mock
    Claims testClaim;

    @Test
    void doInternalFilterTest() throws Exception {
        String username = "some-username";
        String role = UserRole.USER.name();
        String token = "some-jwt-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtTokenProvider.validateTokenAndGetClaim(token)).thenReturn(testClaim);
        when(testClaim.getSubject()).thenReturn(username);

        UserDetails mockUser = new User(
                username,
                "password",
                List.of(new SimpleGrantedAuthority(role))
        );

        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUser);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication, "Authentication should not be null");
        assertEquals(username, authentication.getName());

        assertEquals(role, authentication.getAuthorities().iterator().next().getAuthority());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doInternalFilterTest_whenAuthenticationIsNot() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}