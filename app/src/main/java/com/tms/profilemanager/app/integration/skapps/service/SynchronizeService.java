package com.tms.profilemanager.app.integration.skapps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.profilemanager.app.entity.skapps.SKAppsOutbox;
import com.tms.profilemanager.app.service.skapps.SKAppsOutboxService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SynchronizeService {

    SKAppsOutboxService skAppsOutboxService;
    SkAppsService skAppsService;

    public void synchronize() throws JsonProcessingException {
        List<SKAppsOutbox> merchantOutbox = new ArrayList<>();
        List<SKAppsOutbox> terminalOutbox = new ArrayList<>();

        for (SKAppsOutbox outbox : skAppsOutboxService.findAllByOrderByIdAsc()) {
            switch (outbox.getEntityType()) {
                case MERCHANT -> merchantOutbox.add(outbox);
                case TERMINAL -> terminalOutbox.add(outbox);
            }
        }
        skAppsService.synchronizeAllMerchants(merchantOutbox);
        skAppsService.synchronizeAllTerminals(terminalOutbox);
    }

}
