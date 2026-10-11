package com.tms.profilemanager.app.config.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityManager {

    CurrentUserInfo systemUserInfo;

    @Lazy
    final CustomUserDetailsService userDetailsService;
    final SecurityProperties securityProperties;

    @SneakyThrows
    public CurrentUserInfo getSystemUserInfo() {
        if (systemUserInfo == null) {
            systemUserInfo = (CurrentUserInfo) userDetailsService.loadUserByUsernameAndPassword(
                    securityProperties.getSystemUser().getUsername(), securityProperties.getSystemUser().getPassword());
        }
        return systemUserInfo;
    }

    public void clearCurrentSystemUserInfo() {
        systemUserInfo = null;
    }

    public void runAsSystemUser(Runnable runnable) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(getSystemUserAuthentication());

        runnable.run();

        SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
    }

    public <T> T runAsSystemUser(Supplier<T> supplier) {
        Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(getSystemUserAuthentication());

        T result = supplier.get();

        SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
        return result;
    }

    private Authentication getSystemUserAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                getSystemUserInfo(), null, getSystemUserInfo().getAuthorities());
    }
}
