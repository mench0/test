package com.tms.profilemanager.app.config.security;

import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.User;
import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.dto.enums.Permission;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import static com.tms.profilemanager.app.utils.AuthenticationHolder.getCurrentUserId;
import static com.tms.profilemanager.app.utils.AuthenticationHolder.getCurrentUserInfo;
import static com.tms.profilemanager.app.utils.AuthenticationHolder.getCurrentUserInfoOrElseThrow;

@Component("auth")
public class AuthExpressionsHolder {

    public static boolean isCurrentUser(Long id) {
        return id != null && getCurrentUserId().map(it -> it.equals(id)).orElse(false);
    }

    public static boolean isCurrentUser(String usernameOrEmail) {
        return usernameOrEmail != null && getCurrentUserInfo()
                .map(it -> usernameOrEmail.equals(it.getUsername()) || usernameOrEmail.equals(it.getEmail()))
                .orElse(false);
    }

    public static boolean hasPermission(String permission) {
        return hasPermission(Permission.valueOfByCodeOrElseThrow(permission));
    }

    public static boolean hasPermission(Permission permission) {
        return getCurrentUserInfoOrElseThrow().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission.getCode()));
    }

    public static boolean hasPermissionToWrite(User user) {
        if (isCurrentUser(user.getId())) {
            return true;
        }
        if (user instanceof UsualUser) {
            hasPermission(Permission.USUAL_USER_WRITE);
        }
        if (user instanceof SystemUser) {
            hasPermission(Permission.SYSTEM_USER_WRITE);
        }
        return true;
    }

    public static void throwAccessDeniedException() {
        throw new AccessDeniedException("Access denied");
    }
}
