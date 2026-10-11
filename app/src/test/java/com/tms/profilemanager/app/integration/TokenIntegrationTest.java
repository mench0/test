package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.repository.TokenRepository;
import com.tms.profilemanager.app.repository.UserRepository;
import com.tms.profilemanager.app.service.TokenService;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.enums.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Clock;
import java.time.OffsetDateTime;

import static com.tms.profilemanager.app.entity.Token.TokenType.PASSWORD_RESET;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class TokenIntegrationTest extends AbstractIntegrationTest {

    @Autowired TokenService tokenService;
    @Autowired UserRepository userRepository;
    @Autowired TokenRepository tokenRepository;
    @SpyBean Clock clock;

    Long preparedUserId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        Long roleId = roleGenerator.createAndSave(1, Permission.getAllPermissions()).getId();
        preparedUserId = usualUserGenerator.createAndSave(1, roleId).getId();
    }

    @Test
    void deleteExpiredTokensTest() {
        OffsetDateTime currentTime = ClockUtils.now();
        when(clock.instant()).thenReturn(currentTime.toInstant());
        assertEquals(currentTime, ClockUtils.now());

        final String tokenValue = "token";

        tokenService.addToken(preparedUserId, tokenValue, PASSWORD_RESET);
        tokenService.removeExpiredTokens();
        assertTrue(tokenService.findToken(tokenValue, PASSWORD_RESET).isPresent());
        assertEquals(1, tokenRepository.findByTokenType(PASSWORD_RESET).size());

        currentTime = currentTime.plus(1, HOURS);
        when(clock.instant()).thenReturn(currentTime.toInstant());
        assertEquals(currentTime, ClockUtils.now());

        assertTrue(tokenService.findToken(tokenValue, PASSWORD_RESET).isEmpty());
        tokenService.removeExpiredTokens();
        assertTrue(tokenRepository.findByTokenType(PASSWORD_RESET).isEmpty());

        when(clock.instant()).thenCallRealMethod();
    }
}
