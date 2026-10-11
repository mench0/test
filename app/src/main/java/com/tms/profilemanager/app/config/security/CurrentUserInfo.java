package com.tms.profilemanager.app.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tms.profilemanager.dto.enums.Permission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentUserInfo implements UserDetails {

    Long userId;

    Long roleId;

    String terminalSerialNumber; // not null for system users who linked with terminals

    String username;

    String fullName;

    String email;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    String passwordHash;

    boolean archived;

    @Builder.Default
    Set<Permission> permissions = new HashSet<>();

    Locale locale;

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(Permission::getCode)
                .map(SimpleGrantedAuthority::new)
                .collect(toSet());
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !archived;
    }
}
