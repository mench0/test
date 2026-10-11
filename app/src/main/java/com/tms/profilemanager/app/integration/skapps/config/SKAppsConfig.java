package com.tms.profilemanager.app.integration.skapps.config;

import com.tms.profilemanager.app.integration.skapps.service.SynchronizeService;
import com.tms.profilemanager.app.integration.skapps.job.SKAppsSynchronizeJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SKAppsConfig {

    @Bean
    public SKAppsSynchronizeJob skAppsSynchronizeJob(SynchronizeService synchronizeService) {
        return new SKAppsSynchronizeJob(synchronizeService);
    }
}
