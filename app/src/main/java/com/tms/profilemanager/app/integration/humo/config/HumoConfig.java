package com.tms.profilemanager.app.integration.humo.config;


import com.tms.profilemanager.app.config.MinioProperties;
import com.tms.profilemanager.app.integration.humo.job.HumoJob;
import com.tms.profilemanager.app.integration.humo.service.FileService;
import com.tms.profilemanager.app.integration.humo.service.HumoService;
import com.tms.profilemanager.app.integration.humo.service.OperationService;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.HumoSettingService;
import com.tms.profilemanager.app.service.MerchantService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.TerminalService;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "humo.enable", matchIfMissing = true)
public class HumoConfig {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient humoMinioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public FileService fileService(MinioProperties minioProperties, MinioClient humoMinioClient, HumoSettingService humoSettingService){
        return new FileService(minioProperties, humoMinioClient, humoSettingService);
    }

    @Bean
    public HumoService humoService(TerminalService terminalService, AcquirerService acquirerService,
                                   FileService fileService, OperationService operationService,
                                   HumoSettingService humoSettingService) {
        return new HumoService(terminalService, acquirerService, fileService, operationService, humoSettingService);
    }

    @Bean
    public OperationService operationService(TerminalService terminalService, MerchantService merchantService,
                                             AcquirerService acquirerService, TerminalGroupService terminalGroupService) {
        return new OperationService(terminalService, merchantService, acquirerService, terminalGroupService);
    }

    @Bean
    public HumoJob humoJob(HumoService humoService, FileService fileService) {
        return new HumoJob(humoService, fileService);
    }

}
