package com.tms.profilemanager.app.integration.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "integration")
public class IntegrationProperties {

    @Valid
    @NotNull
    private RKL rkl;

    @Valid
    @NotNull
    private SKApps skapps;

    @NotNull
    private Duration responseTimeout;

    @NotNull
    private Duration connectionTimeout;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RKL {
        @NotNull
        private Boolean customUrlRequired;
        @NotBlank
        private String defaultRequestUrl;
        @NotNull
        private Boolean addUrlToDeviceView;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SKApps {
        @NotBlank
        private String user;
        @NotBlank
        private String password;
        @NotBlank
        private String host;
        @NotBlank
        private String authPath;
        @NotBlank
        private String terminalPath;
        @NotBlank
        private String merchantPath;
        @Valid
        @NotNull
        private Job job;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Job {
        @NotNull
        private Boolean shedlock;
        @NotBlank
        private String fixedDelay;
    }
}

