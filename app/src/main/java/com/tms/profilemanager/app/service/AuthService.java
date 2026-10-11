package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import com.tms.profilemanager.app.config.security.CustomUserDetailsService;
import com.tms.profilemanager.app.utils.JwtHelper;
import com.tms.profilemanager.dto.auth.AuthRequestDto;
import com.tms.profilemanager.dto.auth.AuthResponseDto;
import com.tms.profilemanager.dto.auth.AuthResponseDto.AuthTokenDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    JwtHelper jwtHelper;
    CustomUserDetailsService userDetailsService;
    AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDto login(AuthRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            CurrentUserInfo userInfo = (CurrentUserInfo) authentication.getPrincipal();

            AuthTokenDto accessTokenDto = jwtHelper.generateAccessToken(userInfo);
            AuthTokenDto refreshTokenDto = jwtHelper.generateRefreshToken(userInfo);
            return new AuthResponseDto(accessTokenDto, refreshTokenDto);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(UNAUTHORIZED);
        }
    }

    @Transactional
    public AuthResponseDto refresh(String refreshToken) {
        Long userId = jwtHelper.validateRefreshTokenAndGetUserId(refreshToken);
        CurrentUserInfo userInfo = (CurrentUserInfo) userDetailsService.loadUserById(userId);

        AuthTokenDto refreshTokenDto = jwtHelper.generateRefreshToken(userInfo);
        AuthTokenDto accessTokenDto = jwtHelper.generateAccessToken(userInfo);
        return new AuthResponseDto(accessTokenDto, refreshTokenDto);
    }
}
