package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.integration.skapps.client.SKAppsClient;
import com.tms.profilemanager.app.integration.skapps.dto.SkappsErrorCodeResponseDto;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantCreateRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.merchant.MerchantPatchRequestDto;
import com.tms.profilemanager.app.mapper.filter.MerchantSearchMapper;
import com.tms.profilemanager.app.repository.MerchantRepository;
import com.tms.profilemanager.app.service.provider.MerchantProvider;
import com.tms.profilemanager.app.service.skapps.SKAppsOutboxService;
import com.tms.profilemanager.dto.SKAppsMerchantOutboxPayloadDto;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.enums.EntityType;
import com.tms.profilemanager.dto.filter.MerchantFilterDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.validation.Valid;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

import static com.tms.profilemanager.dto.enums.Permission.MERCHANT_WRITE;

@Slf4j
@Service
public class MerchantService extends AbstractCrudService<Merchant, MerchantCreateDto, MerchantPatchDto, MerchantFilterDto> {

    private final MerchantRepository merchantRepository;
    private final SKAppsClient skAppsClient;
    private final SKAppsOutboxService skAppsOutboxService;

    public MerchantService(MerchantProvider entityProvider,
                           MerchantRepository repository,
                           MerchantSearchMapper searchMapper,
                           ApplicationEventPublisher eventPublisher,
                           SKAppsClient skAppsClient,
                           SKAppsOutboxService skAppsOutboxService) {
        super(Merchant.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.merchantRepository = repository;
        this.skAppsClient = skAppsClient;
        this.skAppsOutboxService = skAppsOutboxService;
    }

    @Override
    @Transactional
    public Merchant create(@Valid MerchantCreateDto createDto) {
        Merchant merchant = super.create(createDto);
        SKAppsMerchantOutboxPayloadDto requestDto = createSKAppsMerchantOutboxPayloadDto(merchant);
        skAppsOutboxService.createOutboxRecord(EntityType.MERCHANT, requestDto, merchant.getId());
        return merchant;
    }

    @Transactional
    public Merchant patch(@Valid MerchantPatchDto patchDto) {
        Merchant entity = getByIdOrElseThrow(patchDto.getId());
        Merchant merchantDiff = mergeMerchant(patchDto, entity);
        assertWriteAccess(patchDto.getId());
        entityProvider.patch(patchDto, entity);
        Merchant savedMerchant = repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new EntityEvent<>(entityClass, entity.getId(), EntityEvent.OperationType.UPDATE));

        if (savedMerchant.getEmail() != null || savedMerchant.getPhone() != null || savedMerchant.getInnOrPinfl() != null) {
            createRecordInOutbox(merchantDiff, savedMerchant);
        }
        return savedMerchant;
    }

    private void createRecordInOutbox(Merchant diffMerchant, Merchant savedMerchant) {
        if (!isMerchantEmpty(diffMerchant)) {
            SKAppsMerchantOutboxPayloadDto requestDto = createSKAppsMerchantOutboxPayloadDto(savedMerchant);
            skAppsOutboxService.createOutboxRecord(EntityType.MERCHANT, requestDto, savedMerchant.getId());
        }
    }

    @Transactional
    public boolean createSKAppsMerchant(MerchantCreateRequestDto requestDto, Long outboxId) {
        try {
            Object response = skAppsClient.createMerchant(requestDto);
            SkappsErrorCodeResponseDto codeResponseDto = (SkappsErrorCodeResponseDto) response;
            if (codeResponseDto != null && codeResponseDto.getCode() != null) {
                if (codeResponseDto.getCode() == 1) {
                    skAppsOutboxService.deleteRecord(outboxId);
                    log.info("Merchant created in SKApps: code = {}", codeResponseDto.getCode());
                    return true;
                } else if (codeResponseDto.getCode() == 0) {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.warn("SKApps returned business error (code=0). Merchant will be retried. Code={}", codeResponseDto.getCode());
                } else if (codeResponseDto.getCode() == 2) {
                    skAppsOutboxService.updateOnBadRequest(outboxId);
                    log.warn("SKApps returned business error (code=2) while creating merchant. Response={}", codeResponseDto.getCode());
                } else {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.error("SKApps returned unexpected business code: {}", codeResponseDto.getCode());
                }
            }
        } catch (Exception e) {
            if (isRetriableError(e)) {
                skAppsOutboxService.updateOnError(outboxId);
            }
            log.error("Failed to create merchant in SKApps", e);
        }
        return false;
    }

    @Transactional
    public void updateSKAppsMerchant(String merchantId, MerchantPatchRequestDto requestDto, Long outboxId) {
        try {
            Object response = skAppsClient.updateMerchant(merchantId, requestDto);
            SkappsErrorCodeResponseDto codeResponseDto = (SkappsErrorCodeResponseDto) response;

            if (codeResponseDto != null && codeResponseDto.getCode() != null) {
                if (codeResponseDto.getCode() == 1) {
                    skAppsOutboxService.deleteRecord(outboxId);
                    log.info("Merchant updated in SKApps: Code=0");
                } else if (codeResponseDto.getCode() == 0) {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.warn("SKApps returned business error (code=0) while updating merchant. Will retry.");
                } else if (codeResponseDto.getCode() == 2) {
                    skAppsOutboxService.updateOnBadRequest(outboxId);
                    log.warn("SKApps returned business error (code=2) while updating merchant.");
                } else {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.error("SKApps returned unexpected business code: {}", codeResponseDto.getCode());
                }
            }
        } catch (Exception e) {
            if (isRetriableError(e)) {
                skAppsOutboxService.updateOnError(outboxId);
            }

            log.error("Failed to update merchant in SKApps", e);
        }
    }

    public boolean merchantExistInSKApps(String innOrPinfl, String merchantId) {
        return skAppsClient.searchMerchantByInnOrPinfl(innOrPinfl).getData().getItems().stream()
                .anyMatch(item -> innOrPinfl.equals(item.getIdentityNumber()) && merchantId.equals(item.getMId()));
    }

    public SKAppsMerchantOutboxPayloadDto createSKAppsMerchantOutboxPayloadDto(Merchant merchant) {
        return SKAppsMerchantOutboxPayloadDto.builder()
                .mId(String.valueOf(merchant.getId()))
                .merchantNameEn(merchant.getName())
                .email(merchant.getEmail())
                .phoneNumber(merchant.getPhone())
                .identityNumber(merchant.getInnOrPinfl())
                .address(merchant.getAddress())
                .build();
    }

    public MerchantCreateRequestDto createMerchantCreateRequestDto(Merchant merchant) {
        MerchantCreateRequestDto result = new MerchantCreateRequestDto();
        result.setMId(String.valueOf(merchant.getId()));
        result.setAddress(merchant.getAddress());
        result.setMerchantNameEn(merchant.getName());
        result.setPhoneNumber(merchant.getPhone());
        result.setEmail(merchant.getEmail());
        result.setIdentityNumber(merchant.getInnOrPinfl());
        return result;
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(MERCHANT_WRITE);
    }

    @Transactional(readOnly = true)
    public Optional<Merchant> getMerchantByName(String name) {
        return merchantRepository.findByName(name);
    }

    private Merchant mergeMerchant(MerchantPatchDto patch, Merchant oldMerchant) {
        Merchant result = new Merchant();

        result.setId(oldMerchant.getId());
        result.setEmail(getUpdatedValue(patch.getEmail(), oldMerchant.getEmail()));
        result.setPhone(getUpdatedValue(patch.getPhone(), oldMerchant.getPhone()));
        result.setName(getUpdatedValue(patch.getName(), oldMerchant.getName()));
        result.setAddress(getUpdatedValue(patch.getAddress(), oldMerchant.getAddress()));
        result.setInnOrPinfl(getUpdatedValue(patch.getAddress(), oldMerchant.getAddress()));

        return result;
    }


    public static <T> T getUpdatedValue(Optional<T> newValueOpt, T oldValue) {
        if (newValueOpt == null || newValueOpt.isEmpty()) {
            return null;
        }
        T newValue = newValueOpt.get();
        if (Objects.equals(newValue, oldValue)) {
            return null;
        }
        return newValue;
    }

    private boolean isMerchantEmpty(Merchant merchant) {
        return merchant.getEmail() == null
                && merchant.getPhone() == null
                && merchant.getName() == null
                && merchant.getAddress() == null
                && merchant.getInnOrPinfl() == null;
    }

    public static boolean isRetriableError(Throwable ex) {
        if (ex instanceof WebClientResponseException w) {
            return w.getStatusCode().is5xxServerError();
        }

        return ex instanceof ConnectException
                || ex instanceof ReadTimeoutException
                || ex instanceof UnknownHostException;
    }
}
