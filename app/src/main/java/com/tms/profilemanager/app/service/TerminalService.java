package com.tms.profilemanager.app.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import com.tms.profilemanager.app.entity.QTerminal;
import com.tms.profilemanager.app.entity.QTerminalAcquirerParams;
import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.integration.rkl.dto.RKLResponseDto;
import com.tms.profilemanager.app.integration.skapps.client.SKAppsClient;
import com.tms.profilemanager.app.integration.skapps.dto.SkappsErrorCodeResponseDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalCreateRequestDto;
import com.tms.profilemanager.app.integration.skapps.dto.terminal.TerminalPatchRequestDto;
import com.tms.profilemanager.app.mapper.entity.TerminalXlsFileMapper;
import com.tms.profilemanager.app.mapper.filter.TerminalSearchMapper;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.app.repository.TerminalAcquirerParamsRepository;
import com.tms.profilemanager.app.repository.TerminalRepository;
import com.tms.profilemanager.app.service.provider.TerminalProvider;
import com.tms.profilemanager.app.service.skapps.SKAppsOutboxService;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.SKAppsTerminalOutboxPayloadDto;
import com.tms.profilemanager.dto.TerminalExportDto;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.enums.EntityType;
import com.tms.profilemanager.dto.filter.TerminalFilterDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import com.tms.profilemanager.dto.request.TerminalSoftwareRequestDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tms.profilemanager.app.service.MerchantService.getUpdatedValue;
import static com.tms.profilemanager.app.service.MerchantService.isRetriableError;
import static com.tms.profilemanager.dto.enums.Permission.Values.TERMINAL_WRITE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TerminalService extends AbstractCrudService<Terminal, TerminalCreateDto, TerminalPatchDto, TerminalFilterDto> {

    TerminalRepository terminalRepository;
    TerminalAcquirerParamsRepository terminalAcquirerParamsRepository;
    SystemUserService systemUserService;
    RoleService roleService;
    TerminalXlsFileMapper terminalXlsFileMapper;
    String defaultTerminalPassword;
    SKAppsClient skAppsClient;
    VendorModelService vendorModelService;
    SKAppsOutboxService skAppsOutboxService;

    public TerminalService(TerminalProvider entityProvider,
                           TerminalRepository repository,
                           TerminalSearchMapper searchMapper,
                           TerminalAcquirerParamsRepository terminalAcquirerParamsRepository,
                           SystemUserService systemUserService,
                           RoleService roleService,
                           ApplicationEventPublisher eventPublisher,
                           TerminalXlsFileMapper terminalXlsFileMapper,
                           @Value("${security.system-user.default-terminal-password}") String defaultTerminalPassword,
                           SKAppsClient skAppsClient,
                           VendorModelService vendorModelService,
                           SKAppsOutboxService skAppsOutboxService) {
        super(Terminal.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.terminalRepository = repository;
        this.terminalAcquirerParamsRepository = terminalAcquirerParamsRepository;
        this.systemUserService = systemUserService;
        this.roleService = roleService;
        this.terminalXlsFileMapper = terminalXlsFileMapper;
        this.defaultTerminalPassword = defaultTerminalPassword;
        this.skAppsClient = skAppsClient;
        this.vendorModelService = vendorModelService;
        this.skAppsOutboxService = skAppsOutboxService;
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_WRITE);
    }

    @Override
    @Transactional
    public Terminal create(@Valid TerminalCreateDto createDto) {
        Terminal terminal = super.create(createDto);
        Optional<SystemUser> linkedSystemUser = systemUserService.getByTerminalSerialNumber(terminal.getSerialNumber());
        if (linkedSystemUser.isEmpty()) {
            systemUserService.create(SystemUserCreateDto.builder()
                    .terminalSerialNumber(terminal.getSerialNumber())
                    .username(terminal.getSerialNumber())
                    .password(defaultTerminalPassword)
                    .fullName(String.format("Terminal (sn: %s)", terminal.getSerialNumber()))
                    .roleId(roleService.getTerminalDefaultRole().getId())
                    .build());
        }
        SKAppsTerminalOutboxPayloadDto requestDto = createSKAppsTerminalOutboxPayloadDto(terminal);
        skAppsOutboxService.createOutboxRecord(EntityType.TERMINAL, requestDto, terminal.getId());
        return terminal;
    }

    @Override
    @Transactional
    public Terminal patch(@Valid TerminalPatchDto patchDto) {
        Terminal terminalBefore = getByIdOrElseThrow(patchDto.getId());
        Terminal terminalDiff = mergeTerminal(patchDto, terminalBefore);
        String beforeSerialNumber = terminalBefore.getSerialNumber();
        assertWriteAccess(patchDto.getId());
        entityProvider.patch(patchDto, terminalBefore);
        Terminal savedTerminal = repository.saveAndFlush(terminalBefore);
        eventPublisher.publishEvent(new EntityEvent<>(entityClass, savedTerminal.getId(), EntityEvent.OperationType.UPDATE));

        systemUserService.changeTerminalSerialNumber(beforeSerialNumber, savedTerminal.getSerialNumber());
        createRecordInOutbox(terminalDiff, savedTerminal);
        return savedTerminal;
    }

    private void createRecordInOutbox(Terminal terminalDiff, Terminal savedTerminal) {
        if (!isTerminalEmpty(terminalDiff)) {
            SKAppsTerminalOutboxPayloadDto requestDto = createSKAppsTerminalOutboxPayloadDto(savedTerminal);
            skAppsOutboxService.createOutboxRecord(EntityType.TERMINAL, requestDto, savedTerminal.getId());
        }
    }

    @Override
    @Transactional
    public void archive(Long id) {
        Terminal terminal = getByIdOrElseThrow(id);
        assertWriteAccess(terminal.getId());
        terminal.setArchived(true);
        terminal.setStatus(0);
        terminal.setLastArchivedAt(ClockUtils.now());
        Terminal savedTerminal = terminalRepository.saveAndFlush(terminal);

        TerminalPatchRequestDto requestDto = patchTerminalRequestDto(savedTerminal);
        skAppsOutboxService.createOutboxRecord(EntityType.TERMINAL, requestDto, savedTerminal.getId());
    }

    @Transactional
    public Terminal moveTerminalToAnotherMerchant(@NotNull Long terminalId, @NotNull Long merchantId) {
        assertWriteAccess();
        archive(terminalId);
        Terminal terminal = getByIdOrElseThrow(terminalId);
        Optional<Terminal> existingCopy = terminalRepository.findBySerialNumberAndMerchantId(terminal.getSerialNumber(), merchantId);
        if (existingCopy.isPresent()) {
            restore(existingCopy.get().getId());
            return existingCopy.get();
        }
        Terminal terminalBasedOn = createTerminalBasedOn(terminal, merchantId);
        setBasesOnTerminalInfo(terminalBasedOn, terminal);
        return repository.save(terminalBasedOn);
    }

    @Transactional(readOnly = true)
    public Optional<Terminal> getTerminalLinkedToCurrentSystemUser() {
        return AuthenticationHolder.getCurrentUserInfo()
                .map(CurrentUserInfo::getTerminalSerialNumber)
                .map(this::getBySerialNumberOrElseThrow);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> countForMerchants(@NotEmpty @Size(max = 50) List<Long> merchantIds) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        return queryFactory.select(QTerminal.terminal.merchant.id, QTerminal.terminal.id.count())
                .from(QTerminal.terminal)
                .where(PredicateBuilder.builder()
                        .in(QTerminal.terminal.merchant.id, merchantIds)
                        .and(searchMapper.getAccessControlPredicate())
                        .build())
                .groupBy(QTerminal.terminal.merchant.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        it -> it.get(0, Long.class),
                        it -> requireNonNull(it.get(1, Long.class))));
    }

    @Transactional
    public void processRklResponse(Terminal terminal, RKLResponseDto response) {
        if (isEmpty(response.getEncryptedKeys())) {
            return;
        }
        response.getEncryptedKeys().forEach(it -> terminal.getTerminalAcquirerParams().stream()
                .filter(params -> params.getAcquirerId().equals(it.getAcquirerId())
                                && params.getIsoTerminalId().equals(it.getTid())
                                && params.getIsoMerchantId().equals(it.getMid()))
                .findFirst()
                .ifPresent(params -> {
                    params.setPinMasterKeyKCV(it.getPinMasterKeyKCV());
                    params.setKcvUpdatedDate(ClockUtils.now());
                }));
        terminalRepository.saveAndFlush(terminal);
    }

    @Transactional
    public void setConfigUpdatedDate(Terminal terminal) {
        terminal.setConfigUpdatedDate(ClockUtils.now());
        terminalRepository.save(terminal);
    }

    @Transactional
    public void updateTerminalSoftwareInfo(Terminal terminal, TerminalSoftwareRequestDto terminalSoftwareRequestDto) {
        setTerminalSoftwareInfo(terminal, terminalSoftwareRequestDto);
        Terminal savedTerminal = repository.saveAndFlush(terminal);
        eventPublisher.publishEvent(new EntityEvent<>(entityClass, savedTerminal.getId(), EntityEvent.OperationType.UPDATE));

        skAppsOutboxService.createOutboxRecord(EntityType.TERMINAL,
                createSKAppsTerminalOutboxPayloadDto(savedTerminal), savedTerminal.getId());
    }

    /**
     * Custom method for query creation to add custom fetch joins
     */
    @SuppressWarnings("unchecked")
    protected JPAQuery<Terminal> createQuery(Predicate predicate, Sort sort, Integer pageNumber, Integer pageSize) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        QTerminalAcquirerParams terminalAcquirerParams = new QTerminalAcquirerParams("tap");

        JPAQuery<Terminal> query = (JPAQuery<Terminal>) queryFactory.from(QTerminal.terminal)
                .distinct().setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .leftJoin(QTerminal.terminal.group).fetchJoin()
                .leftJoin(QTerminal.terminal.merchant).fetchJoin()
                .leftJoin(QTerminal.terminal.terminalAcquirerParams, terminalAcquirerParams).fetchJoin()
                .leftJoin(terminalAcquirerParams.availableOperations).fetchJoin();

        if (pageNumber != null && pageSize != null) {
            // select needed terminal ids to avoid query with join fetch + pagination
            JPAQuery<Long> queryIds = queryFactory.select(QTerminal.terminal.id).from(QTerminal.terminal).where(predicate);
            applyPageable(queryIds, PageRequest.of(pageNumber, pageSize, sort), entityManager);
            List<Long> terminalIds = queryIds.fetch();

            query.where(QTerminal.terminal.id.in(terminalIds));
        } else {
            query.where(predicate);
            applySort(query, sort, entityManager);
        }
        return query;
    }

    private Terminal getBySerialNumberOrElseThrow(@NotNull String serialNumber) {
        Predicate predicate = PredicateBuilder.builder()
                .eq(QTerminal.terminal.serialNumber, serialNumber)
                .eq(QTerminal.terminal.archived, false)
                .eq(QTerminal.terminal.status, 1)
                .build();
        Terminal terminal = createQuery(predicate).fetchOne();
        if (terminal == null) {
            throw new EntityNotFoundByFieldException(Terminal.class, Terminal.Fields.serialNumber, serialNumber);
        }
        return terminal;
    }

    private Terminal createTerminalBasedOn(Terminal terminal, Long merchantId) {
        TerminalCreateDto createDto = TerminalCreateDto.builder()
                .serialNumber(terminal.getSerialNumber())
                .modelId(terminal.getVendorModelId())
                .defaultTerminalType(terminal.getDefaultTerminalType())
                .merchantId(merchantId)
                .servicePin(terminal.getServicePin())
                .address(terminal.getAddress())
                .country(terminal.getCountry())
                .terminalAcquirerParams(terminal.getTerminalAcquirerParams().stream()
                        .map(it -> TerminalAcquirerParamsInputDto.builder()
                                .merchantName(it.getMerchantName())
                                .isoTerminalId(it.getIsoTerminalId())
                                .isoMerchantId(it.getIsoMerchantId())
                                .acquirerId(it.getAcquirerId())
                                .pinMasterKeyKCV(it.getPinMasterKeyKCV())
                                .type(it.getType())
                                .mcc(it.getMcc())
                                .availableOperations(it.getOrderedAvailableOperations())
                                .paramsPaymentTypes(it.getParamsPaymentTypes())
                                .build())
                        .collect(toList()))
                .groupId(Optional.ofNullable(terminal.getGroup()).map(TerminalGroup::getId).orElse(null))
                .status(terminal.getStatus())
                .build();
        return create(createDto);
    }

    private void setTerminalSoftwareInfo(Terminal terminal, TerminalSoftwareRequestDto terminalSoftwareRequestDto) {
        terminal.setSoftwareVersion(terminalSoftwareRequestDto.getSoftwareVersion());
        terminal.setSoftwareName(terminalSoftwareRequestDto.getSoftwareName());
        terminal.setMode(terminalSoftwareRequestDto.getMode());
        terminal.setSoftwareVendor(terminalSoftwareRequestDto.getSoftwareVendor());
        terminal.setSoftwareModel(terminalSoftwareRequestDto.getSoftwareModel());
    }

    private void setBasesOnTerminalInfo(Terminal terminalBasedOn, Terminal terminal) {
        terminalBasedOn.setSoftwareVersion(terminal.getSoftwareVersion());
        terminalBasedOn.setSoftwareName(terminal.getSoftwareName());
        terminalBasedOn.setMode(terminal.getMode());
        terminalBasedOn.setSoftwareVendor(terminal.getSoftwareVendor());
        terminalBasedOn.setSoftwareModel(terminal.getSoftwareModel());
        terminalBasedOn.setConfigUpdatedDate(terminal.getConfigUpdatedDate());
    }

    @Transactional(readOnly = true)
    public Map<String, Terminal> getTerminalsBySNsMap(Set<String> serialNumbers) {
        return terminalRepository.findBySerialNumberIsInMap(serialNumbers);
    }

    @Transactional(readOnly = true)
    public Map<Long, Set<TerminalExportDto>> findTerminalsByGroupIds(Set<Long> ids) {
        return terminalXlsFileMapper.toExportDto(getTerminalsByGroupIds(ids));
    }

    @Transactional(readOnly = true)
    public Set<TerminalAcquirerParamsRepository.TerminalAcquirerParamsSlim> getTerminalsByGroupIds(Set<Long> ids) {
        return terminalAcquirerParamsRepository.findParamsByTerminalGroupIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<Terminal> findFirstByVendorModelIdIn(Set<Long> ids) {
        return terminalRepository.findFirstByVendorModelIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<Terminal> findTerminalsByMerchantId(Long id) {
        return terminalRepository.findByMerchantIdAndArchivedFalseAndStatus(id, 1);
    }

    @Transactional
    public void createSKAppsTerminal(TerminalCreateRequestDto createRequestDto, Long outboxId) {
        try {
            Object response = skAppsClient.createTerminal(createRequestDto);
            SkappsErrorCodeResponseDto codeResponseDto = (SkappsErrorCodeResponseDto) response;

            if (codeResponseDto != null && codeResponseDto.getCode() != null) {
                if (codeResponseDto.getCode() == 1) {
                    skAppsOutboxService.deleteRecord(outboxId);
                    log.info("Terminal created in SKApps: Code=0");
                } else if (codeResponseDto.getCode() == 0) {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.warn("SKApps returned business error (code=0) while creating terminal. Will retry.");
                } else if (codeResponseDto.getCode() == 2) {
                    skAppsOutboxService.updateOnBadRequest(outboxId);
                    log.warn("SKApps returned business error (code=2) while creating terminal.");
                } else {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.error("SKApps returned unexpected business code: {}", codeResponseDto.getCode());
                }
            }
        } catch (Exception e) {
            if (isRetriableError(e)) {
                skAppsOutboxService.updateOnError(outboxId);
            }
            log.error("Failed to create terminal in SKApps", e);
        }
    }

    @Transactional
    public void updateSKAppsTerminal(Long terminalId, TerminalPatchRequestDto requestDto, Long outboxId) {
        try {
            Object response = skAppsClient.updateTerminal(terminalId, requestDto);
            SkappsErrorCodeResponseDto codeResponseDto = (SkappsErrorCodeResponseDto) response;

            if (codeResponseDto != null && codeResponseDto.getCode() != null) {
                if (codeResponseDto.getCode() == 1) {
                    skAppsOutboxService.deleteRecord(outboxId);
                    log.info("Terminal updated in SKApps: {}", response);
                } else if (codeResponseDto.getCode() == 0) {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.warn("SKApps returned business error (code=0) while updating terminal. Will retry.");
                } else if (codeResponseDto.getCode() == 2) {
                    skAppsOutboxService.updateOnBadRequest(outboxId);
                    log.warn("SKApps returned business error (code=2) while updating terminal.");
                } else {
                    skAppsOutboxService.updateOnError(outboxId);
                    log.error("SKApps returned unexpected business code: {}", codeResponseDto.getCode());
                }
            }
        } catch (Exception e) {
            if (isRetriableError(e)) {
                skAppsOutboxService.updateOnError(outboxId);
            }
            log.error("Failed to update terminal in SKApps", e);
        }
    }

    private TerminalPatchRequestDto patchTerminalRequestDto(Terminal terminal) {
        return vendorModelService.getByIdUsingCacheWithoutAccessControl(terminal.getVendorModelId())
                .map(model -> TerminalPatchRequestDto.builder()
                        .vendor(model.getVendor().getName())
                        .model(model.getName())
                        .statusId(terminal.getStatus())
                        .softwareVersion(terminal.getSoftwareVersion())
                        .build())
                .orElseThrow(() -> new EntityNotFoundByFieldException(VendorModel.class, "id", terminal.getVendorModelId()));
    }

    public SKAppsTerminalOutboxPayloadDto createSKAppsTerminalOutboxPayloadDto(Terminal terminal) {
        return vendorModelService.getByIdUsingCacheWithoutAccessControl(terminal.getVendorModelId())
                .map(model -> {
                    SKAppsTerminalOutboxPayloadDto createRequestDto = new SKAppsTerminalOutboxPayloadDto();
                    createRequestDto.setTId(String.valueOf(terminal.getId()));
                    createRequestDto.setMId(String.valueOf(terminal.getMerchantId()));
                    createRequestDto.setSerialNumber(terminal.getSerialNumber());
                    createRequestDto.setVendor(model.getVendor().getName());
                    createRequestDto.setModel(model.getName());
                    createRequestDto.setStatusId(terminal.getStatus());
                    createRequestDto.setSoftwareVersion(terminal.getSoftwareVersion());
                    return createRequestDto;
                })
                .orElseThrow(() -> new EntityNotFoundByFieldException(VendorModel.class, "id", terminal.getVendorModelId()));
    }

    public TerminalCreateRequestDto createTerminalCreateRequestDto(Terminal terminal) {
        return vendorModelService.getByIdUsingCacheWithoutAccessControl(terminal.getVendorModelId())
                .map(model -> {
                    TerminalCreateRequestDto createRequestDto = new TerminalCreateRequestDto();
                    createRequestDto.setTId(String.valueOf(terminal.getId()));
                    createRequestDto.setMId(String.valueOf(terminal.getMerchantId()));
                    createRequestDto.setSerialNumber(terminal.getSerialNumber());
                    createRequestDto.setVendor(model.getVendor().getName());
                    createRequestDto.setModel(model.getName());
                    createRequestDto.setStatusId(terminal.getStatus());
                    createRequestDto.setSoftwareVersion(terminal.getSoftwareVersion());
                    return createRequestDto;
                })
                .orElseThrow(() -> new EntityNotFoundByFieldException(VendorModel.class, "id", terminal.getVendorModelId()));
    }

    public boolean terminalExistInSKApps(String serialNumber, String terminalId) {
        return skAppsClient.searchTerminalBySerialNumber(serialNumber).getData().getItems().stream()
                .anyMatch(item -> serialNumber.equals(item.getSerialNumber()) && terminalId.equals(item.getTId()));
    }

    private Terminal mergeTerminal(TerminalPatchDto patch, Terminal old) {
        Terminal result = new Terminal();

        result.setId(old.getId());
        result.setMerchantId(old.getMerchantId());
        result.setSerialNumber(getUpdatedValue(patch.getSerialNumber(), old.getSerialNumber()));
        result.setVendorModelId(getUpdatedValue(patch.getModelId(), old.getVendorModelId()));
        result.setStatus(getUpdatedValue(patch.getStatus(), old.getStatus()));

        return result;
    }

    private boolean isTerminalEmpty(Terminal terminal) {
        return terminal.getId() == null
                && terminal.getMerchantId() == null
                && terminal.getSerialNumber() == null
                && terminal.getVendorModelId() == null
                && terminal.getStatus() == null;
    }
}
