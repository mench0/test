package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.PaymentScreenLogo;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.TerminalGroupPaymentScreenLogo;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.exception.EntityNotFoundByIdException;
import com.tms.profilemanager.app.repository.PaymentScreenLogoRepository;
import com.tms.profilemanager.dto.create.PaymentScreenLogoCreateDto;
import com.tms.profilemanager.dto.patch.PaymentScreenLogoPatchDto;
import com.tms.profilemanager.dto.util.PaymentScreenLogoImage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SCREEN_LOGO_READ;
import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SCREEN_LOGO_WRITE;
import static java.util.Objects.requireNonNull;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaymentScreenLogoService {

    private final static String PAYMENT_SCREEN_LOGO_IMAGE_BY_ID_CACHE = "payment-screen-logo-image-by-id";

    PaymentScreenLogoRepository paymentScreenLogoRepository;
    PaymentScreenLogoImageService paymentScreenLogoImageService;
    TerminalGroupService terminalGroupService;
    CacheManager cacheManager;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('" + PAYMENT_SCREEN_LOGO_READ + "')")
    public List<PaymentScreenLogo> findAllLogos() {
        return paymentScreenLogoRepository.findAll().stream()
                .sorted(Comparator.comparing(PaymentScreenLogo::getCreatedAt, Comparator.naturalOrder()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentScreenLogo> findAllLogoByGroup(TerminalGroup terminalGroup) {
        return new ArrayList<>(
                terminalGroupService.findSelfAndParents(terminalGroup.getId()).stream()
                        .sorted(Comparator.comparingInt(
                                tg -> tg.getParentPath() == null ? 0 : tg.getParentPath().split("\\.").length
                        ))
                        .flatMap(group -> group.getPaymentScreenLogos().stream()
                                .sorted(Comparator.comparing(
                                        TerminalGroupPaymentScreenLogo::getPaymentScreenLogoOrder,
                                        Comparator.nullsLast(Integer::compareTo)
                                ))
                        )
                        .map(TerminalGroupPaymentScreenLogo::getPaymentScreenLogo)
                        .collect(Collectors.toMap(
                                PaymentScreenLogo::getId,
                                Function.identity(),
                                (existing, ignored) -> existing,
                                LinkedHashMap::new
                        ))
                        .values()
        );
    }

    @Transactional(propagation = Propagation.NEVER)
    @PreAuthorize("hasAuthority('" + PAYMENT_SCREEN_LOGO_WRITE + "')")
    public void delete(Long id) {
        PaymentScreenLogo logo = findLogoById(id);
        paymentScreenLogoImageService.deleteImageFromMinio(logo.getImageId());
        paymentScreenLogoRepository.delete(logo);
    }

    @Transactional(propagation = Propagation.NEVER)
    @PreAuthorize("hasAuthority('" + PAYMENT_SCREEN_LOGO_WRITE + "')")
    public void upload(MultipartFile file, PaymentScreenLogoCreateDto inputDto) {
        String fileId = paymentScreenLogoImageService.uploadToMinio(file);
        try {
            paymentScreenLogoRepository.save(PaymentScreenLogo.builder().name(inputDto.getName()).imageId(fileId).build());
        } catch (Exception e) {
            paymentScreenLogoImageService.deleteImageFromMinio(fileId);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public PaymentScreenLogo findLogoById(Long logoId) {
        return paymentScreenLogoRepository.findById(logoId)
                .orElseThrow(() -> new EntityNotFoundByIdException(PaymentScreenLogo.class, logoId));
    }

    @Transactional(readOnly = true)
    public PaymentScreenLogo findLogoByImageId(String imageId) {
        return paymentScreenLogoRepository.findByImageId(imageId)
                .orElseThrow(() -> new EntityNotFoundByFieldException(PaymentScreenLogo.class, "imageId", imageId));
    }

    @Transactional(propagation = Propagation.NEVER)
    @PreAuthorize("hasAuthority('" + PAYMENT_SCREEN_LOGO_WRITE + "')")
    public void update(Long id, PaymentScreenLogoPatchDto paymentScreenLogoPatchDto, MultipartFile file) {
        PaymentScreenLogo logo = findLogoById(id);

        if (file != null && !file.isEmpty()) {
            String newLogoFileId = paymentScreenLogoImageService.uploadToMinio(file);
            logo.setImageId(newLogoFileId);
        }

        logo.setName(paymentScreenLogoPatchDto.getName());
        paymentScreenLogoRepository.save(logo);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(EntityEvent<?> event) {
        if (event.entityClass().equals(PaymentScreenLogo.class)) {
            requireNonNull(cacheManager.getCache(PAYMENT_SCREEN_LOGO_IMAGE_BY_ID_CACHE)).evict(event.entityId());
        }
    }

    @Cacheable(cacheNames = PAYMENT_SCREEN_LOGO_IMAGE_BY_ID_CACHE, unless = "#result == 0")
    public PaymentScreenLogoImage downloadByImageId(String imageId) {
        return paymentScreenLogoImageService.downloadFile(imageId);
    }

}
