package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.Token;
import com.tms.profilemanager.app.repository.TokenRepository;
import com.tms.profilemanager.app.utils.ClockUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenService {

    @Value("${security.token.password-reset.life-time-in-minutes:5}")
    int passwordResetTokenLifeTimeInMinutes;

    final PasswordEncoder passwordEncoder;
    final TokenRepository tokenRepository;

    @Transactional
    public Token addToken(Long userId, String tokenValue, Token.TokenType tokenType) {
        if (findToken(tokenValue, tokenType).isPresent()) {
            return null;
        }
        tokenRepository.deleteByUserIdAndTokenType(userId, tokenType);
        return tokenRepository.save(Token.builder()
                .userId(userId)
                .tokenHash(passwordEncoder.encode(tokenValue))
                .tokenType(tokenType)
                .createdAt(ClockUtils.now())
                .lifeTimeInMinutes(switch (tokenType) {
                    case PASSWORD_RESET -> passwordResetTokenLifeTimeInMinutes;
                })
                .build());
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        tokenRepository.deleteByUserIdAndTokenType(userId, Token.TokenType.PASSWORD_RESET);
    }

    @Transactional
    @Scheduled(fixedDelayString = "${security.remove-expired-tokens-delay-in-minutes:5}", timeUnit = TimeUnit.MINUTES)
    public void removeExpiredTokens() {
        tokenRepository.deleteExpiredTokens(ClockUtils.now());
    }

    @Transactional(readOnly = true)
    public Optional<Token> findToken(String token, Token.TokenType tokenType) {
        return findTokenInList(tokenRepository.findByTokenType(tokenType), token);
    }

    @Transactional(readOnly = true)
    public Optional<Token> findToken(Long userId, String token, Token.TokenType tokenType) {
        return findTokenInList(tokenRepository.findByUserIdAndTokenType(userId, tokenType), token);
    }

    private Optional<Token> findTokenInList(List<Token> tokens, String token) {
        List<Token> foundTokens = tokens.stream()
                .filter(it -> passwordEncoder.matches(token, it.getTokenHash()))
                .collect(Collectors.toList());
        if (foundTokens.size() > 1) {
            tokenRepository.deleteAll(foundTokens);
            throw new IllegalStateException(foundTokens.size() + " identical tokens found");
        }
        if (isEmpty(foundTokens)) {
            return Optional.empty();
        }
        Token foundToken = foundTokens.iterator().next();
        if (isExpired(foundToken)) {
            tokenRepository.delete(foundToken);
            return Optional.empty();
        }
        return Optional.of(foundToken);
    }

    private boolean isExpired(Token token) {
        if (token.getCreatedAt() == null) {
            return true;
        }
        return token.getCreatedAt().isBefore(ClockUtils.now().minus(token.getLifeTimeInMinutes(), MINUTES));
    }
}
