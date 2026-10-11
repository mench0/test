package com.tms.profilemanager.app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import com.tms.profilemanager.dto.auth.AuthResponseDto.AuthTokenDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtHelper {

    ObjectMapper objectMapper;

    @Value("${security.jwt.secret}")
    String secret;

    @Value("${security.token.access.life-time-in-minutes:30}")
    int accessTokenLifeTimeInMinutes;
    @Value("${security.token.refresh.life-time-in-days:7}")
    int refreshTokenLifeTimeInDays;

    private static final String USER_INFO_CLAIM = "userInfo";
    private static final String USER_ID_CLAIM = "userId";

    public AuthTokenDto generateAccessToken(CurrentUserInfo userInfo) {
        try {
            OffsetDateTime now = ClockUtils.now();
            String token = JWT.create()
                    .withClaim(USER_INFO_CLAIM, objectMapper.writeValueAsString(userInfo))
                    .withIssuedAt(now.toInstant())
                    .withExpiresAt(now.plusMinutes(accessTokenLifeTimeInMinutes).toInstant())
                    .sign(Algorithm.HMAC256(secret));
            return new AuthTokenDto(token, accessTokenLifeTimeInMinutes);
        } catch (Exception e) {
            throw new JWTCreationException("Error during creation jwt token", e);
        }
    }

    public AuthTokenDto generateRefreshToken(CurrentUserInfo userInfo) {
        try {
            OffsetDateTime now = ClockUtils.now();
            String token = JWT.create()
                    .withClaim(USER_ID_CLAIM, userInfo.getUserId())
                    .withIssuedAt(now.toInstant())
                    .withExpiresAt(now.plusDays(refreshTokenLifeTimeInDays).toInstant())
                    .sign(Algorithm.HMAC256(secret));
            return new AuthTokenDto(token, refreshTokenLifeTimeInDays * 24 * 60);
        } catch (Exception e) {
            throw new JWTCreationException("Error during creation jwt token", e);
        }
    }

    public CurrentUserInfo validateAccessTokenAndGetUserInfo(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return objectMapper.readValue(jwt.getClaim(USER_INFO_CLAIM).asString(), CurrentUserInfo.class);
        } catch (Exception e) {
            throw new JWTVerificationException("Error during jwt access token verification", e);
        }
    }

    public Long validateRefreshTokenAndGetUserId(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim(USER_ID_CLAIM).asLong();
        } catch (Exception e) {
            throw new JWTVerificationException("Error during jwt refresh token verification", e);
        }
    }
}
