package sanets.dev.animechallenges.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.lifetime}")
    private int lifetime;

    public String generateToken(String username, String role){

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // died line of token (24 hours)
        long expMillis = nowMillis + lifetime;
        Date exp = new Date(expMillis);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateTokenAndGetClaim(String token){

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
