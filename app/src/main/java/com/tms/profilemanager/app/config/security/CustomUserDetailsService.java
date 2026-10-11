package com.tms.profilemanager.app.config.security;

import com.tms.profilemanager.app.mapper.entity.UserMapper;
import com.tms.profilemanager.app.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {

    @Lazy
    SecurityManager securityManager;
    UserService userService;
    UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return securityManager.runAsSystemUser(() ->
                userService.getByUsernameOrEmail(usernameOrEmail)
                        .map(userMapper::toCurrentUserInfo)
                        .orElseThrow(() -> new UsernameNotFoundException("User with username or email " + usernameOrEmail + " not found")));
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) {
        return securityManager.runAsSystemUser(() ->
                userService.getById(userId)
                        .map(userMapper::toCurrentUserInfo)
                        .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found")));
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsernameAndPassword(String usernameOrEmail, String password) throws UsernameNotFoundException {
        return userService.getByUsernameOrEmail(usernameOrEmail, password)
                        .map(userMapper::toCurrentUserInfo)
                        .orElseThrow(() -> new UsernameNotFoundException("User with username or email " + usernameOrEmail + " not found"));
    }
}
