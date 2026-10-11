package com.tms.profilemanager.app.integration.humo.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "humo")
public class HumoProperties {

    @NotNull
    private Job job;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Job {
        @NotNull
        private Boolean shedlock;
        @NotBlank
        private String scheduler;
    }
}
