package com.tms.profilemanager.app.integration.humo.service;


import com.neovisionaries.i18n.CountryCode;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.integration.humo.dto.MerchantHumo;
import com.tms.profilemanager.app.integration.humo.dto.TerminalHumo;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.MerchantService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tms.profilemanager.app.integration.humo.dto.MerchantHumo.OperationType.I;
import static com.tms.profilemanager.app.integration.humo.dto.MerchantHumo.OperationType.U;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REPLACE;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OperationService {

    static String GNK_MCC = "9399";
    TerminalService terminalService;
    MerchantService merchantService;
    AcquirerService acquirerService;
    TerminalGroupService terminalGroupService;

    @Transactional
    public void updateTypeOperation(TerminalHumo terminalHumo, Terminal terminal, HumoSetting humoSetting, Acquirer acquirer) {
        MerchantHumo merchantHumo = terminalHumo.getMerchant();
        Map<Long, String> acquirerIdNameMap = getAcquirerIdNameMapForTerminal(terminal);

        Long humoAcquirerId = humoSetting.getAcquirerId();
        boolean exists = terminal.getTerminalAcquirerParams().stream().anyMatch(p -> humoAcquirerId.equals(p.getAcquirerId()));

        List<TerminalAcquirerParamsInputDto> allParams = terminal.getTerminalAcquirerParams().stream()
                .map(params -> {
                    if (humoAcquirerId.equals(params.getAcquirerId())) {
                        log.info("У терминала с serialNumber={} уже имелся экваер {}, поэтому обновили данные экваера.",
                                terminal.getSerialNumber(), acquirerIdNameMap.get(humoAcquirerId));
                        return TerminalAcquirerParamsInputDto.builder()
                                .merchantName(merchantHumo.getMid() + "/" + merchantHumo.getTid())
                                .isoMerchantId(merchantHumo.getMid())
                                .isoTerminalId(merchantHumo.getTid())
                                .acquirerId(params.getAcquirerId())
                                .pinMasterKeyKCV(params.getPinMasterKeyKCV())
                                .rkiEnabled(params.isRkiEnabled())
                                .mcc(merchantHumo.getMcc())
                                .type(humoSetting.getTerminalType())
                                .paramsPaymentTypes(params.getParamsPaymentTypes())
                                .availableOperations(params.getAvailableOperations().stream()
                                        .map(TerminalAcquirerOperation::getOperation)
                                        .collect(Collectors.toList()))
                                .build();
                    }
                    return createExistParamsInputDto(params);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        if (!exists) {
            allParams.add(createParamsInputDto(merchantHumo, humoSetting, acquirer));
            log.info("У терминала с serialNumber={} не нашли экваера {}, поэтому добавили.",
                    terminal.getSerialNumber(), acquirerIdNameMap.get(humoAcquirerId));
        }

        updateTerminalAndMerchant(terminalHumo, terminal, humoSetting, allParams);
        log.info("Обновили терминал с serialNumber={} и мерчанта с id={}", terminalHumo.getSerialNr(), terminal.getMerchant().getId());
    }

    @Transactional
    public void insertTypeOperation(TerminalHumo terminalHumo, Terminal terminal, HumoSetting humoSetting, Acquirer acquirer) {
        try {
            addHumoAcquirerIfNotExist(terminalHumo, terminal, humoSetting, acquirer);
        } catch (Exception e) {
            log.error("Не смогли записать в базу терминал с serialNumber={}", terminalHumo.getSerialNr(), e);
        }
        log.info("Записали терминал с serialNumber={}", terminalHumo.getSerialNr());
    }

    @Transactional
    public void deleteTypeOperation(TerminalHumo terminalHumo, Terminal terminal, HumoSetting humoSetting) {
        try {
            if (terminal.getTerminalAcquirerParams().size() <= 1) {
                terminalService.delete(terminal.getId());
                log.debug("Удалили терминал с id={}", terminal.getId());
            } else {
                List<TerminalAcquirerParamsInputDto> acquirerWithoutHumo = terminal.getTerminalAcquirerParams().stream()
                        .filter(terminalAcquirerParams -> !humoSetting.getAcquirerId().equals(terminalAcquirerParams.getAcquirerId()))
                        .map(this::createExistParamsInputDto)
                        .collect(Collectors.toList());
                updateTerminal(terminalHumo, terminal, humoSetting, acquirerWithoutHumo);
                log.info("Удалили экваер Humo у терминала с id={}", terminal.getId());
            }
        } catch (Exception e) {
            log.error("Не смогли выполнить операцию на удаление терминала с id={}", terminal.getId(), e);
        }
    }

    @Transactional
    public void operationIfTerminalNotFound(TerminalHumo terminalHumo, MerchantHumo.OperationType typeHumo, HumoSetting humoSetting, Acquirer acquirer) {
        try {
            if (U.equals(typeHumo) || I.equals(typeHumo)) {
                MerchantHumo merchantHumo = terminalHumo.getMerchant();
                Optional<Merchant> merchant = merchantService.getMerchantByName(merchantHumo.getName());
                if (merchant.isEmpty()) {
                    Merchant savedMerchant = createMerchant(merchantHumo);
                    createTerminal(terminalHumo, humoSetting, savedMerchant.getId(), acquirer);
                    log.info("""
                            В базе не нашли ни терминал с serialNumber={} ни мерчанта по имени {}.\
                            Поэтому создали нового мерчанта и терминал.\
                            """, terminalHumo.getSerialNr(), merchantHumo.getName());
                } else {
                    createTerminal(terminalHumo, humoSetting, merchant.get().getId(), acquirer);
                    log.info("""
                            В базе не наши терминал с serialNumber={}, но нашли мерчанта.\
                            Поэтому создали новый терминал и привязали мерчанта с именем {}\
                            """, terminalHumo.getSerialNr(), merchantHumo.getName());
                }
            }
        } catch (Exception e) {
            log.error("Не удалось выполнить операцию создания терминала, в случае когда терминал не найден", e);
        }
    }

    private void updateTerminalAndMerchant(TerminalHumo terminalHumo, Terminal terminal, HumoSetting humoSetting, List<TerminalAcquirerParamsInputDto> allParams) {
        MerchantHumo merchantHumo = terminalHumo.getMerchant();
        if (terminal.getMerchant().getName().equals(merchantHumo.getName())) {
            updateMerchantAddress(merchantHumo, terminal.getMerchant().getId());
            updateTerminal(terminalHumo, terminal, humoSetting, allParams);
            log.info("Мерчант {} не изменился, поэтому обновили адрес мерчанта и данные терминала с serialNumber {}",
                    merchantHumo.getName(), terminalHumo.getSerialNr());
        } else {
            Optional<Merchant> merchant = merchantService.getMerchantByName(merchantHumo.getName());
            Terminal newTerminal = null;
            if (merchant.isEmpty()) {
                log.info("В базе не нашли мерчанта {}, поэтому создаем новый", merchantHumo.getName());
                Merchant newMerchant = createMerchant(merchantHumo);
                newTerminal = terminalService.moveTerminalToAnotherMerchant(terminal.getId(), newMerchant.getId());
                log.info("Терминалу с serialNumber {} был присвоен новый мерчант {}", terminalHumo.getSerialNr(), merchantHumo.getName());
            } else {
                newTerminal = terminalService.moveTerminalToAnotherMerchant(terminal.getId(), merchant.get().getId());
                log.info("Мерчант уже существовал, поэтому терминал с serialNumber {} был присвоен мерчанту {}",
                        terminalHumo.getSerialNr(), merchantHumo.getName());
            }
            updateTerminal(terminalHumo, newTerminal, humoSetting, allParams);
        }
    }

    private void updateTerminal(TerminalHumo terminalHumo, Terminal terminal, HumoSetting humoSetting, List<TerminalAcquirerParamsInputDto> acquirerParams) {
        MerchantHumo merchantHumo = terminalHumo.getMerchant();
        TerminalGroup group = getTerminalGroup(terminalHumo);
        terminalService.patch(TerminalPatchDto.builder()
                .id(terminal.getId())
                .serialNumber(Optional.of(terminalHumo.getSerialNr()))
                .modelId(Optional.of(humoSetting.getModelId()))
                .defaultTerminalType(Optional.of(humoSetting.getTerminalType()))
                .servicePin(Optional.of(humoSetting.getServicePin()))
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(acquirerParams, REPLACE))
                .address(Optional.of(merchantHumo.getCity() + ", " + merchantHumo.getStreet()))
                .groupId(Optional.of(group.getId()))
                .build());
    }

    private void updateMerchantAddress(MerchantHumo merchantHumo, Long merchantId) {
        merchantService.patch(MerchantPatchDto.builder()
                .id(merchantId)
                .address(Optional.of(merchantHumo.getCity() + ", " + merchantHumo.getStreet()))
                .build());
    }

    private void createTerminal(TerminalHumo terminalHumo, HumoSetting humoSetting, Long merchantId, Acquirer acquirer) {
        MerchantHumo merchantHumo = terminalHumo.getMerchant();
        TerminalGroup group = getTerminalGroup(terminalHumo);
        terminalService.create(TerminalCreateDto.builder()
                .serialNumber(terminalHumo.getSerialNr())
                .modelId(humoSetting.getModelId())
                .defaultTerminalType(humoSetting.getTerminalType())
                .servicePin(humoSetting.getServicePin())
                .country(CountryCode.UZ.getName())
                .merchantId(merchantId)
                .terminalAcquirerParams(List.of(TerminalAcquirerParamsInputDto.builder()
                        .merchantName(merchantHumo.getMid() + "/" + merchantHumo.getTid())
                        .isoTerminalId(merchantHumo.getTid())
                        .isoMerchantId(merchantHumo.getMid())
                        .availableOperations(acquirer.getOrderedAvailableOperations())
                        .acquirerId(acquirer.getId())
                        .mcc(merchantHumo.getMcc())
                        .type(humoSetting.getTerminalType())
                        .paramsPaymentTypes(List.of())
                        .build()))
                .groupId(group.getId())
                .address(merchantHumo.getCity() + ", " + merchantHumo.getStreet())
                .build());
    }

    private Merchant createMerchant(MerchantHumo merchantHumo) {
        return merchantService.create(MerchantCreateDto.builder()
                .address(merchantHumo.getCity() + ", " + merchantHumo.getStreet())
                .name(merchantHumo.getName()).build());
    }

    private void addHumoAcquirerIfNotExist(TerminalHumo terminalHumo, Terminal terminal, HumoSetting humoSetting, Acquirer acquirer) {
        Map<Long, String> acquirerIdNameMap = getAcquirerIdNameMapForTerminal(terminal);
        if (isHumoAcquirerNotExist(terminal, humoSetting)) {
            MerchantHumo merchantHumo = terminalHumo.getMerchant();
            List<TerminalAcquirerParamsInputDto> params = terminal.getTerminalAcquirerParams().stream()
                    .map(this::createExistParamsInputDto)
                    .collect(Collectors.toList());
            params.add(createParamsInputDto(merchantHumo, humoSetting, acquirer));
            updateTerminal(terminalHumo, terminal, humoSetting, params);
            log.info("Терминал с serialNumber={} и без экваера {} уже существовал, поэтому только обновили данные экваера.",
                    terminal.getSerialNumber(), acquirerIdNameMap.get(humoSetting.getAcquirerId()));
        }
    }

    private TerminalAcquirerParamsInputDto createParamsInputDto(MerchantHumo merchantHumo, HumoSetting humoSetting, Acquirer acquirer) {
        return TerminalAcquirerParamsInputDto.builder()
                .merchantName(merchantHumo.getMid() + "/" + merchantHumo.getTid())
                .isoMerchantId(merchantHumo.getMid())
                .isoTerminalId(merchantHumo.getTid())
                .acquirerId(acquirer.getId())
                .mcc(merchantHumo.getMcc())
                .type(humoSetting.getTerminalType())
                .availableOperations(acquirer.getOrderedAvailableOperations())
                .paramsPaymentTypes(List.of())
                .build();
    }

    private TerminalAcquirerParamsInputDto createExistParamsInputDto(TerminalAcquirerParams params) {
        return TerminalAcquirerParamsInputDto.builder()
                .merchantName(params.getMerchantName())
                .isoMerchantId(params.getIsoMerchantId())
                .isoTerminalId(params.getIsoTerminalId())
                .acquirerId(params.getAcquirerId())
                .rkiEnabled(params.isRkiEnabled())
                .pinMasterKeyKCV(params.getPinMasterKeyKCV())
                .mcc(params.getMcc())
                .type(params.getType())
                .availableOperations(params.getOrderedAvailableOperations())
                .paramsPaymentTypes(params.getParamsPaymentTypes())
                .build();
    }

    private boolean isHumoAcquirerNotExist(Terminal terminal, HumoSetting humoSetting) {
        return terminal.getTerminalAcquirerParams().stream()
                .map(TerminalAcquirerParams::getAcquirerId)
                .noneMatch(acquirerId -> acquirerId.equals(humoSetting.getAcquirerId()));
    }

    private Map<Long, String> getAcquirerIdNameMapForTerminal(Terminal terminal) {
        return terminal.getTerminalAcquirerParams().stream()
                .map(params -> acquirerService.getByIdUsingCacheWithoutAccessControl(params.getAcquirerId()).orElseThrow())
                .collect(toMap(Acquirer::getId, Acquirer::getName));
    }

    private TerminalGroup getTerminalGroup(TerminalHumo terminalHumo) {
        return GNK_MCC.equals(terminalHumo.getMerchant().getMcc())
                ? terminalGroupService.findOrCreateGNKGroupByName()
                : terminalGroupService.findOrCreateGroupByName(terminalHumo.getBankId());
    }
}
