package com.tms.profilemanager.app.config;

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

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    @NotBlank
    private String url;
    @NotBlank
    private String accessKey;
    @NotBlank
    private String secretKey;
    @Valid
    @NotNull
    private Humo humo;
    @Valid
    @NotNull
    private SKApps skApps;
    @Valid
    @NotNull
    private PaymentScreenLogo paymentScreenLogo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Humo {
        @NotBlank
        private String bucket;
        @NotBlank
        private String errorBucket;
        @NotBlank
        private String rejectBucket;
        @NotNull
        private Integer maxKeys;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SKApps {
        @NotBlank
        private String bucket;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentScreenLogo {
        @NotBlank
        private String bucket;
    }

}
