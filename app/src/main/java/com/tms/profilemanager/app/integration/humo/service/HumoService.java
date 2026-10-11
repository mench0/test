package com.tms.profilemanager.app.integration.humo.service;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.exception.EntityNotFoundByIdException;
import com.tms.profilemanager.app.integration.humo.dto.MerchantHumo;
import com.tms.profilemanager.app.integration.humo.dto.TerminalHumo;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.HumoSettingService;
import com.tms.profilemanager.app.service.TerminalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class HumoService {

    private final TerminalService terminalService;

    private final AcquirerService acquirerService;

    private final FileService fileService;

    private final OperationService operationService;

    private final HumoSettingService humoSettingService;

    @RunAsSystemUser
    public void runHumoHandler() {
        Set<Map<String, TerminalHumo>> terminalsMap = fileService.download();
        Map<String, HumoSetting> humoSettings = humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl().stream()
                .collect(Collectors.toMap(HumoSetting::getHumoId, Function.identity()));

        terminalsMap.forEach(terminalMap -> {
            Map<String, Terminal> terminalsFromDB = terminalService.getTerminalsBySNsMap(terminalMap.keySet().stream()
                    .map(it -> it.substring(5))
                    .collect(Collectors.toSet()));
            int i = terminalMap.size();
            for (Map.Entry<String, TerminalHumo> humoEntry : terminalMap.entrySet()) {
                TerminalHumo terminalHumo = humoEntry.getValue();
                terminalHumo.setSerialNr(terminalHumo.getSerialNr().substring(5));

                String humoId = humoEntry.getKey().substring(0, 5);
                HumoSetting humoSetting = humoSettings.get(humoId);
                Acquirer acquirer = acquirerService.getByIdUsingCacheWithoutAccessControl(humoSetting.getAcquirerId())
                        .orElseThrow(() -> new EntityNotFoundByIdException(Acquirer.class, humoSetting.getAcquirerId()));

                Terminal terminal = terminalsFromDB.get(terminalHumo.getSerialNr());
                MerchantHumo.OperationType typeHumo = terminalHumo.getMerchant().getType();
                if (terminal != null) {
                    switch (typeHumo) {
                        case U -> operationService.updateTypeOperation(terminalHumo, terminal, humoSetting, acquirer);
                        case I -> operationService.insertTypeOperation(terminalHumo, terminal, humoSetting, acquirer);
                        case D -> operationService.deleteTypeOperation(terminalHumo, terminal, humoSetting);
                    }
                } else {
                    operationService.operationIfTerminalNotFound(terminalHumo, typeHumo, humoSetting, acquirer);
                }
                if (i < 2) fileService.removeFileFromMinio(humoId + terminalHumo.getSerialNr());
                i--;
            }
        });
        fileService.getSerialNumberToFileName().clear();
    }
}
