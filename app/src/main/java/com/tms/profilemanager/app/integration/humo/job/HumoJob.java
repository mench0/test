package com.tms.profilemanager.app.integration.humo.job;

import com.tms.profilemanager.app.integration.humo.service.FileService;
import com.tms.profilemanager.app.integration.humo.service.HumoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class HumoJob {

    private final HumoService humoService;

    private final FileService fileService;

    @Scheduled(cron = "${humo.job.scheduler}")
    @SchedulerLock(name = "HumoScheduledTask", lockAtMostFor = "PT5M")
    public void job() {
        log.info("Запуск ФП для загрузки файлов из minio");
        try {
            humoService.runHumoHandler();
        } catch (Exception e) {
            fileService.getSerialNumberToFileName().clear();
            log.error("Ошибка при обработке файлов, полученных из minio.", e);
            return;
        }
        log.info("Все файлы из minio обработаны");
    }
}
