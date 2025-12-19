package sanets.dev.animechallenges.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JwtTokenProviderTest {
    JwtTokenProvider jwtTokenProvider =  new JwtTokenProvider();

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "some-very-long-and-secure-secret-key-for-testing-at-least-256-bits");
        ReflectionTestUtils.setField(jwtTokenProvider, "lifetime", 3600000);
    }

    @Test
    void generateToken(){
        String username = "username";
        UUID userUid = UUID.randomUUID();
        String correctHashedPassword = "correctHashedPassword";

        User testUser = User.builder()
                .uid(userUid)
                .username(username)
                .passwordHash(correctHashedPassword)
                .role(UserRole.ROLE_USER)
                .build();

        String generatedToken = jwtTokenProvider.generateToken(testUser.getUid(), testUser.getUsername(), testUser.getRole().name());

        Claims claim = jwtTokenProvider.validateTokenAndGetClaim(generatedToken);
        System.out.println(claim);

        assertEquals(testUser.getUsername(), claim.getSubject());
        assertEquals(testUser.getUid().toString(), claim.get("uid"));
        assertEquals(testUser.getRole().name(), claim.get("role"));
    }
}
