package com.tms.profilemanager.app.config.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "security")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityProperties {

    @NotNull
    SystemUserProperties systemUser;

    @Data
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SystemUserProperties {

        @NotNull
        String username;

        @NotNull
        String password;
    }
}
