package sanets.dev.animechallenges.service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.security.JwtTokenProvider;

@Service
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String generateToken(User user){
        return jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
    }

    public Claims validateToken(String token){
        return jwtTokenProvider.validateTokenAndGetClaim(token);
    }
}
