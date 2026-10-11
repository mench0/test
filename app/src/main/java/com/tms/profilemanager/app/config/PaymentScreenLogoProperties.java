package com.tms.profilemanager.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;


@Data
@Component
@ConfigurationProperties(prefix = "payment-screen-logo")
public class PaymentScreenLogoProperties {

    @NotNull
    private List<String> contentTypes;
    @NotNull
    private DataSize maxFileSize;
    @NotNull
    private Duration imageCacheTtl;

}
