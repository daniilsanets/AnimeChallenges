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
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.service.JwtService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    JwtService jwtService;

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

        when(request.getHeader("Authorization")).thenReturn("Bearer " +  token);
        when(jwtService.validateToken(token)).thenReturn(testClaim);
        when(testClaim.getSubject()).thenReturn(username);
        when(testClaim.get("role")).thenReturn(role);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);

        assertEquals(new SimpleGrantedAuthority(role), authentication.getAuthorities().iterator().next());
        assertEquals(username, authentication.getName());
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
