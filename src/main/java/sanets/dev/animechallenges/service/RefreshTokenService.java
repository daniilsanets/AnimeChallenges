package sanets.dev.animechallenges.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import sanets.dev.animechallenges.exception.auth.TokenRefreshException;
import sanets.dev.animechallenges.model.security.RefreshToken;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.repository.RefreshTokenRepository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.REFRESH_TOKEN_NOT_FOUND_MSG;

@Slf4j
@Service
public class RefreshTokenService {

    @Value("${jwt.refreshTokenDurationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.deleteByUserUid(user.getUid());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(OffsetDateTime.now().plus(refreshTokenDurationMs, ChronoUnit.MILLIS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) throws TokenRefreshException, HttpClientErrorException {
        if (refreshToken.getExpiryDate().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            log.info("Refresh token expired!");
            throw new TokenRefreshException(REFRESH_TOKEN_NOT_FOUND_MSG);
        }
        return refreshToken;
    }
}
