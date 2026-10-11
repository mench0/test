package com.tms.profilemanager.app.integration.skapps.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "skapps")
public class SKAppsProperties {

    @NotNull
    private List<String> contentTypes;
    @NotNull
    private DataSize fileSize;

}
