package com.tms.profilemanager.app.integration.humo.service;

import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.integration.humo.dto.MerchantHumo;
import com.tms.profilemanager.app.integration.humo.dto.TerminalHumo;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.HumoSettingService;
import com.tms.profilemanager.app.service.TerminalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.tms.profilemanager.app.integration.humo.service.OperationServiceTest.getAcquirer;
import static com.tms.profilemanager.app.integration.humo.service.OperationServiceTest.getHumoSettings;
import static com.tms.profilemanager.app.integration.humo.service.OperationServiceTest.getTerminal;
import static com.tms.profilemanager.app.integration.humo.service.OperationServiceTest.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumoServiceTest {

    @Mock
    TerminalService terminalService;
    @Mock
    AcquirerService acquirerService;
    @Mock
    FileService fileService;
    @Mock
    OperationService operationService;
    @Mock
    HumoSettingService humoSettingService;
    @InjectMocks
    HumoService humoService;

    @Test
    void runHumoHandlerForInsertWhenTerminalExist() {
        String fileName = "/TMS_I_01124_1210231428.xml";
        Map<String, TerminalHumo> readFile = readFile(fileName);
        Map<String, Terminal> terminalMap = terminalFromDb("225153585");

        when(humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl()).thenReturn(getHumoSettings());
        when(fileService.download()).thenReturn(Set.of(readFile));
        when(terminalService.getTerminalsBySNsMap(terminalMap.keySet())).thenReturn(terminalMap);
        when(acquirerService.getByIdUsingCacheWithoutAccessControl(getHumoSettings().get(0).getAcquirerId())).thenReturn(Optional.of(getAcquirer()));

        assertEquals(MerchantHumo.OperationType.I, readFile.get("ERA93225153585").getMerchant().getType());

        humoService.runHumoHandler();

        verify(operationService).insertTypeOperation(any(), any(), any(), any());
        verify(fileService).removeFileFromMinio(any());
        verify(operationService, times(0)).updateTypeOperation(any(), any(), any(), any());
        verify(operationService, times(0)).deleteTypeOperation(any(), any(), any());
        verify(operationService, times(0)).operationIfTerminalNotFound(any(), any(), any(), any());
    }

    @Test
    void runHumoHandlerForInsertWhenIfTerminalNotExist() {
        String fileName = "/TMS_I_01124_1210231428.xml";
        Map<String, TerminalHumo> readFile = readFile(fileName);

        when(terminalService.getTerminalsBySNsMap(any())).thenReturn(Collections.emptyMap());
        when(humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl()).thenReturn(getHumoSettings());
        when(fileService.download()).thenReturn(Set.of(readFile));
        when(acquirerService.getByIdUsingCacheWithoutAccessControl(getHumoSettings().get(0).getAcquirerId())).thenReturn(Optional.of(getAcquirer()));

        assertEquals(MerchantHumo.OperationType.I, readFile.get("ERA93225153585").getMerchant().getType());

        humoService.runHumoHandler();

        verify(operationService).operationIfTerminalNotFound(any(), any(), any(), any());
        verify(fileService).removeFileFromMinio(any());
        verify(operationService, times(0)).updateTypeOperation(any(), any(), any(), any());
        verify(operationService, times(0)).deleteTypeOperation(any(), any(), any());
        verify(operationService, times(0)).insertTypeOperation(any(), any(), any(), any());

    }


    @Test
    void runHumoHandlerForUpdateWhenTerminalExist() {
        String fileName = "/TMS_U_00959_2108201136.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Map<String, Terminal> terminalMap = terminalFromDb("3507993768");
        MerchantHumo.OperationType operationType = humoMap.get("ERA933507993768").getMerchant().getType();

        when(humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl()).thenReturn(getHumoSettings());
        when(fileService.download()).thenReturn(Set.of(humoMap));
        when(terminalService.getTerminalsBySNsMap(terminalMap.keySet())).thenReturn(terminalMap);
        when(acquirerService.getByIdUsingCacheWithoutAccessControl(getHumoSettings().get(0).getAcquirerId())).thenReturn(Optional.of(getAcquirer()));

        assertEquals(MerchantHumo.OperationType.U, operationType);

        humoService.runHumoHandler();

        verify(operationService).updateTypeOperation(any(), any(), any(), any());
        verify(fileService).removeFileFromMinio(any());
        verify(operationService, times(0)).operationIfTerminalNotFound(any(), any(), any(), any());
        verify(operationService, times(0)).deleteTypeOperation(any(), any(), any());
        verify(operationService, times(0)).insertTypeOperation(any(), any(), any(), any());

    }

    @Test
    void runHumoHandlerForDeleteWhenTerminalExist() {
        String fileName = "/TMS_D_00083_2108201352.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Map<String, Terminal> terminalMap = terminalFromDb("2510142018");
        MerchantHumo.OperationType operationType = humoMap.get("ERA932510142018").getMerchant().getType();

        when(humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl()).thenReturn(getHumoSettings());
        when(fileService.download()).thenReturn(Set.of(humoMap));
        when(terminalService.getTerminalsBySNsMap(Set.of("2510142018"))).thenReturn(terminalMap);
        when(acquirerService.getByIdUsingCacheWithoutAccessControl(getHumoSettings().get(0).getAcquirerId())).thenReturn(Optional.of(getAcquirer()));

        assertEquals(MerchantHumo.OperationType.D, operationType);

        humoService.runHumoHandler();

        verify(operationService).deleteTypeOperation(any(), any(), any());
        verify(fileService).removeFileFromMinio(any());
        verify(operationService, times(0)).operationIfTerminalNotFound(any(), any(), any(), any());
        verify(operationService, times(0)).updateTypeOperation(any(), any(), any(), any());
        verify(operationService, times(0)).insertTypeOperation(any(), any(), any(), any());

    }

    public static Map<String, Terminal> terminalFromDb(String sn) {
        Map<String, Terminal> res = new HashMap<>();
        Terminal terminal = getTerminal(sn);
        res.put(sn, terminal);
        return res;
    }
}