package sanets.dev.animechallenges.service;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.security.JwtTokenProvider;

@Slf4j
@Service
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String generateToken(User user){
        log.debug("Jwt generate token");
        return jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
    }

    public Claims validateToken(String token){
        log.debug("Jwt validate token");
        return jwtTokenProvider.validateTokenAndGetClaim(token);
    }
}
