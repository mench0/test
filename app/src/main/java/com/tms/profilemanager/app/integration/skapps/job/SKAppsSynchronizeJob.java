package com.tms.profilemanager.app.integration.skapps.job;

import com.tms.profilemanager.app.integration.skapps.service.SynchronizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class SKAppsSynchronizeJob {

    private final SynchronizeService synchronizeService;

    @Scheduled(fixedDelayString = "${integration.skapps.job.fixed-delay}")
    @SchedulerLock(name = "SKAppsScheduledTask", lockAtLeastFor = "PT10S", lockAtMostFor = "PT10M")
    public void job() {
        log.info("Запуск ФП для синхронизации данных с SKApps");
        try {
            synchronizeService.synchronize();
        } catch (Exception e) {
            log.error("Ошибка при cинхронизации с SKApps.", e);
            return;
        }
        log.info("Данные с SKApps синхронизированы");
    }
}
