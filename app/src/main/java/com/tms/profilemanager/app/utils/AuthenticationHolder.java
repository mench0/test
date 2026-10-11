package com.tms.profilemanager.app.utils;

import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import com.tms.profilemanager.app.mapper.entity.UserMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Data
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationHolder {

    UserMapper userMapper;

    public static Optional<CurrentUserInfo> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of((CurrentUserInfo) authentication.getPrincipal());
    }

    public static CurrentUserInfo getCurrentUserInfoOrElseThrow() {
        return getCurrentUserInfo().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    public static Optional<Long> getCurrentUserId() {
        return getCurrentUserInfo().map(CurrentUserInfo::getUserId);
    }

    public static Long getCurrentUserIdOrElseThrow() {
        return getCurrentUserId().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
