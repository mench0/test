package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.MinioProperties;
import com.tms.profilemanager.app.entity.ReceiptTemplate;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.exception.DuplicateException;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.exception.FileContentTypeException;
import com.tms.profilemanager.app.exception.FileNotFoundException;
import com.tms.profilemanager.app.exception.FileSizeExceededException;
import com.tms.profilemanager.app.exception.InvalidPeriodOrderException;
import com.tms.profilemanager.app.exception.InvalidPeriodPastException;
import com.tms.profilemanager.app.integration.skapps.config.SKAppsProperties;
import com.tms.profilemanager.app.repository.ReceiptTemplateRepository;
import com.tms.profilemanager.app.service.provider.ReceiptTemplateProvider;
import com.tms.profilemanager.dto.create.ReceiptTemplateCreateDto;
import com.tms.profilemanager.dto.patch.ReceiptTemplatePatchDto;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tms.profilemanager.app.entity.event.EntityEvent.OperationType.INSERT;
import static com.tms.profilemanager.app.entity.event.EntityEvent.OperationType.UPDATE;
import static com.tms.profilemanager.dto.enums.Permission.Values.RECEIPT_TEMPLATE_WRITE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptTemplateService {

    private final static String RECEIPT_TEMPLATE_BY_MERCHANT_ID_CACHE = "receipt-template-by-merchant-id";

    private final CacheManager cacheManager;
    private final ReceiptTemplateRepository receiptTemplateRepository;
    private final ReceiptTemplateProvider receiptTemplateProvider;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final SKAppsProperties skAppsProperties;
    private final MerchantService merchantService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @PreAuthorize("hasAuthority('" + RECEIPT_TEMPLATE_WRITE + "')")
    public ReceiptTemplate createReceiptTemplate(ReceiptTemplateCreateDto createDto, String logoFileId) {
        OffsetDateTime now = OffsetDateTime.now();
        validatePeriod(createDto.getValidFrom(), createDto.getValidTo(), now);
        validateUniquenessOnCreate(createDto.getMerchantId(), createDto.getExternalId(), createDto.getValidFrom(), createDto.getValidTo(), now);
        merchantService.existsByIdOrElseThrow(createDto.getMerchantId());
        ReceiptTemplate entity = receiptTemplateProvider.toEntity(createDto);
        entity.setLogoFileId(logoFileId);
        ReceiptTemplate receiptTemplate = receiptTemplateRepository.save(entity);
        eventPublisher.publishEvent(
                new EntityEvent<>(ReceiptTemplate.class, receiptTemplate.getMerchantId(), INSERT)
        );

        return receiptTemplate;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + RECEIPT_TEMPLATE_WRITE + "')")
    public ReceiptTemplate updateReceiptTemplate(ReceiptTemplatePatchDto patchDto, ReceiptTemplate receiptTemplate, String logoFileId) {
        OffsetDateTime now = OffsetDateTime.now();
        validatePeriod(receiptTemplate.getValidFrom(), receiptTemplate.getValidTo(), now);
        validateUniquenessOnPatch(receiptTemplate.getMerchantId(), receiptTemplate.getExternalId(), receiptTemplate.getValidFrom(), receiptTemplate.getValidTo(), now);
        merchantService.getByIdOrElseThrow(patchDto.getMerchantId());
        receiptTemplateProvider.toEntity(patchDto, receiptTemplate);
        receiptTemplate.setLogoFileId(logoFileId);
        ReceiptTemplate template = receiptTemplateRepository.save(receiptTemplate);
        eventPublisher.publishEvent(
                new EntityEvent<>(ReceiptTemplate.class, receiptTemplate.getMerchantId(), UPDATE)
        );
        return template;
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + RECEIPT_TEMPLATE_WRITE + "')")
    public void deactivateReceiptTemplate(Long externalId) {
        ReceiptTemplate receiptTemplate = findReceiptTemplate(externalId);
        receiptTemplate.setActive(false);
        receiptTemplateRepository.save(receiptTemplate);
        eventPublisher.publishEvent(
                new EntityEvent<>(ReceiptTemplate.class, receiptTemplate.getMerchantId(), UPDATE)
        );
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + RECEIPT_TEMPLATE_WRITE + "')")
    public void activateReceiptTemplate(Long externalId) {
        ReceiptTemplate receiptTemplate = findReceiptTemplate(externalId);
        OffsetDateTime now = OffsetDateTime.now();
        validatePeriod(receiptTemplate.getValidFrom(), receiptTemplate.getValidTo(), now);
        validateUniquenessOnPatch(receiptTemplate.getMerchantId(), receiptTemplate.getExternalId(), receiptTemplate.getValidFrom(), receiptTemplate.getValidTo(), now);
        receiptTemplate.setActive(true);
        receiptTemplateRepository.save(receiptTemplate);
        eventPublisher.publishEvent(
                new EntityEvent<>(ReceiptTemplate.class, receiptTemplate.getMerchantId(), UPDATE)
        );
    }

    public ReceiptTemplate findReceiptTemplate(Long externalId) {
        return receiptTemplateRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundByFieldException(ReceiptTemplate.class, "externalId", externalId));
    }

    private void validatePeriod(OffsetDateTime from, OffsetDateTime to, OffsetDateTime now) {
        if (!from.isBefore(to)) {
            throw new InvalidPeriodOrderException(from, to);
        }

        if (!to.isAfter(now)) {
            throw new InvalidPeriodPastException(to, now);
        }

    }

    private void validateUniquenessOnCreate(Long merchantId, Long externalId, OffsetDateTime from, OffsetDateTime to, OffsetDateTime now) {
        if (receiptTemplateRepository.countActiveReceiptTemplate(merchantId, from, to, now, externalId) > 0) {
            throw new DuplicateException("ReceiptTemplate");
        }

    }

    private void validateUniquenessOnPatch(Long merchantId, Long externalId, OffsetDateTime from, OffsetDateTime to, OffsetDateTime now) {
        if (receiptTemplateRepository.countActiveReceiptTemplate(merchantId, from, to, now, externalId) > 1) {
            throw new DuplicateException("ReceiptTemplate");
        }

    }

    @SneakyThrows
    public byte[] downloadFile(Long merchantId) {
        ReceiptTemplate receiptTemplate = getReceiptTemplateByMerchantId(merchantId);

        if (receiptTemplate == null) {
            return null;
        }

        try (InputStream in = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getSkApps().getBucket())
                        .object(receiptTemplate.getLogoFileId())
                        .build()
        )) {
            return in.readAllBytes();
        }
    }

    public ReceiptTemplate getReceiptTemplateByMerchantId(Long merchantId) {
        Cache cache = cacheManager.getCache(RECEIPT_TEMPLATE_BY_MERCHANT_ID_CACHE);
        ReceiptTemplate cached = cache != null ? cache.get(merchantId, ReceiptTemplate.class) : null;

        OffsetDateTime now = OffsetDateTime.now();
        if (cached != null && cached.getValidTo().isAfter(now)) {
            return cached;
        }

        ReceiptTemplate fromDb = receiptTemplateRepository.findByMerchantId(merchantId).orElse(null);

        if (fromDb != null && cache != null && fromDb.getValidTo().isAfter(now)) {
            cache.put(merchantId, fromDb);
        }

        return fromDb;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(EntityEvent<?> event) {
        if (event.entityClass().equals(ReceiptTemplate.class)) {
            Cache cache = cacheManager.getCache(RECEIPT_TEMPLATE_BY_MERCHANT_ID_CACHE);
            if (cache != null) {
                cache.evict(event.entityId());
            }
        }
    }

    public String uploadToMinio(MultipartFile file) {
        validateFile(file);
        try {
            String objectName = String.valueOf(UUID.randomUUID());

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioProperties.getSkApps().getBucket())
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            return objectName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public void removeFileFromMinio(String logoFileId) {
        try {
            MinioProperties.SKApps skApps = minioProperties.getSkApps();
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(skApps.getBucket()).object(logoFileId).build());
            log.info("Из minio удалили файл с названием {}", logoFileId);
        } catch (Exception e) {
            log.error("Не смогли удалить файл из minio", e);
        }
    }

    public void validateFile(MultipartFile file) {
        if (!isAllowedContentType(file.getContentType())) {
            throw new FileContentTypeException(skAppsProperties.getContentTypes());
        }

        long maxSizeBytes = skAppsProperties.getFileSize().toBytes();

        if (file.isEmpty()) {
            throw new FileNotFoundException();
        }

        if (file.getSize() > maxSizeBytes) {
            throw new FileSizeExceededException(skAppsProperties.getFileSize());
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return skAppsProperties.getContentTypes().contains(contentType);
    }
}
