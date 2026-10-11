package com.tms.profilemanager.app.service.skapps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.profilemanager.app.entity.skapps.SKAppsOutbox;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.repository.SKAppsOutboxRepository;
import com.tms.profilemanager.dto.enums.EntityType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SKAppsOutboxService {

    SKAppsOutboxRepository skAppsOutboxRepository;
    ObjectMapper objectMapper;

    @Transactional
    public void deleteRecord(Long id) {
        skAppsOutboxRepository.deleteById(id);
        log.debug("SKAppsOutbox record deleted, recordId={}", id);
    }

    @Transactional
    public void updateOnError(Long id) {
        SKAppsOutbox record = skAppsOutboxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByFieldException(SKAppsOutbox.class, "id", id));
        record.setRetryCount(record.getRetryCount() + 1);
        skAppsOutboxRepository.save(record);
        log.debug("SKAppsOutbox retryCount incremented, recordId={}, eventType={}, newRetryCount={}",
                record.getId(), record.getEntityType(), record.getRetryCount()
        );
    }

    @Transactional
    public void updateOnBadRequest(Long id) {
        SKAppsOutbox record = skAppsOutboxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByFieldException(SKAppsOutbox.class, "id", id));
        record.setBadRequest(true);
        skAppsOutboxRepository.save(record);
        log.debug("SKAppsOutbox marked as badRequest=true, recordId={}, eventType={}", record.getId(), record.getEntityType());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createOutboxRecord(EntityType entityType, Object object, Long entityId) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error while creating outbox record", e);
        }
        skAppsOutboxRepository.save(SKAppsOutbox.builder()
                .entityId(entityId)
                .entityType(entityType)
                .payload(payload)
                .build());
    }

    @Transactional
    public List<SKAppsOutbox> findAllByOrderByIdAsc() {
        return skAppsOutboxRepository.findAllByIsBadRequestIsFalseOrderByIdAsc();
    }
}
