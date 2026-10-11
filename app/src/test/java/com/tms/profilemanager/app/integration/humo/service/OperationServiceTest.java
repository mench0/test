package com.tms.profilemanager.app.integration.humo.service;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.Merchant;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.integration.humo.dto.MerchantHumo;
import com.tms.profilemanager.app.integration.humo.dto.TerminalHumo;
import com.tms.profilemanager.app.integration.humo.dto.Terminals;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.MerchantService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.tms.profilemanager.dto.enums.AcquirerOperationType.SALE;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REPLACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    TerminalService terminalService;
    @Mock
    MerchantService merchantService;
    @Mock
    AcquirerService acquirerService;
    @Mock
    TerminalGroupService terminalGroupService;
    @InjectMocks
    OperationService operationService;

    @Test
    void insertTypeOperationWhenTerminalExist() {
        String fileName = "/TMS_I_01124_1210231428.xml";
        Map<String, TerminalHumo> readFile = readFile(fileName);
        Terminal terminal = getTerminal("225153585");
        TerminalHumo terminalHumo = readFile.get("ERA93225153585");
        Optional<VendorModel> vendorModel = Optional.of(VendorModel.builder().id(1L).name("PAX_A930").vendor(Vendor.builder().id(1L).name("PAX").build()).build());

        when(terminalGroupService.findOrCreateGroupByName(any())).thenReturn(TerminalGroup.builder().id(1L).name("1111_ERA").build());

        assertEquals(MerchantHumo.OperationType.I, readFile.get("ERA93225153585").getMerchant().getType());

        operationService.insertTypeOperation(terminalHumo, terminal, getHumoSettings().get(0), getAcquirer());

        verify(terminalService).patch(TerminalPatchDto.builder()
                .id(terminal.getId())
                .serialNumber(Optional.of(terminalHumo.getSerialNr()))
                .modelId(Optional.of(vendorModel.get().getId()))
                .defaultTerminalType(Optional.of(getHumoSettings().get(0).getTerminalType()))
                .servicePin(Optional.of(getHumoSettings().get(0).getServicePin()))
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder()
                        .merchantName(terminalHumo.getMerchant().getMid() + "/" + terminalHumo.getMerchant().getTid())
                        .isoMerchantId(terminalHumo.getMerchant().getMid())
                        .isoTerminalId(terminalHumo.getMerchant().getTid())
                        .mcc(terminalHumo.getMerchant().getMcc())
                        .type(getHumoSettings().get(0).getTerminalType())
                        .acquirerId(getAcquirer().getId())
                        .availableOperations(getAcquirer().getOrderedAvailableOperations())
                        .paramsPaymentTypes(List.of()).build()), REPLACE))
                .address(Optional.of("Toshkent shahar, Bektemir Bektemir"))
                .groupId(Optional.of(1L))
                .build());
        verify(merchantService, times(0)).create(any());
        verify(terminalService, times(0)).create(any());
        verify(terminalService, times(0)).delete(any(Long.class));
    }

    @Test
    void insertTypeOperationIfTerminalNotFound() {
        String fileName = "/TMS_I_01124_1210231428.xml";
        Map<String, TerminalHumo> readFile = readFile(fileName);
        Terminal terminal = getTerminal("225153585");
        Merchant merchant = terminal.getMerchant();
        TerminalHumo terminalHumo = readFile.get("ERA93225153585");
        MerchantHumo.OperationType operationType = terminalHumo.getMerchant().getType();
        Optional<VendorModel> vendorModel = Optional.of(VendorModel.builder().id(1L).name("PAX_A930").vendor(Vendor.builder().id(1L).name("PAX").build()).build());
        when(merchantService.getMerchantByName(any())).thenReturn(Optional.of(getMerchant()));
        when(terminalGroupService.findOrCreateGroupByName(any())).thenReturn(TerminalGroup.builder().id(1L).name("1111_ERA").build());

        assertEquals(MerchantHumo.OperationType.I, terminalHumo.getMerchant().getType());

        operationService.operationIfTerminalNotFound(terminalHumo, operationType, getHumoSettings().get(0), getAcquirer());
        verify(terminalService).create(TerminalCreateDto.builder()
                .serialNumber(terminalHumo.getSerialNr())
                .modelId(1L)
                .modelId(vendorModel.get().getId())
                .defaultTerminalType(getHumoSettings().get(0).getTerminalType())
                .servicePin(getHumoSettings().get(0).getServicePin())
                .country(CountryCode.UZ.getName())
                .merchantId(merchant.getId())
                .terminalAcquirerParams(List.of(TerminalCreateDto.TerminalAcquirerParamsInputDto.builder()
                        .merchantName(terminalHumo.getMerchant().getMid() + "/" + terminalHumo.getMerchant().getTid())
                        .isoTerminalId(terminalHumo.getMerchant().getTid())
                        .isoMerchantId(terminalHumo.getMerchant().getMid())
                        .mcc(terminalHumo.getMerchant().getMcc())
                        .type(getHumoSettings().get(0).getTerminalType())
                        .availableOperations(List.of(SALE))
                        .acquirerId(getAcquirer().getId())
                        .paramsPaymentTypes(List.of()).build()))
                .groupId(1L)
                .address("Toshkent shahar, Bektemir Bektemir")
                .build());
        verify(merchantService, times(0)).create(any());
        verify(terminalService, times(0)).patch(any());
        verify(terminalService, times(0)).delete(any(Long.class));
    }

    @Test
    void updateTypeOperationWhenTerminalExist() {
        String fileName = "/TMS_U_00959_2108201136.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Terminal terminal = getTerminal("3507993768");
        TerminalHumo terminalHumo = humoMap.get("ERA933507993768");
        MerchantHumo merchantHumo = terminalHumo.getMerchant();
        MerchantHumo.OperationType operationType = merchantHumo.getType();
        Optional<VendorModel> vendorModel = Optional.of(VendorModel.builder().id(1L).name("PAX_A930").vendor(Vendor.builder().id(1L).name("PAX").build()).build());

        Merchant merchantFromDb = Merchant.builder().id(1L)
                .name(merchantHumo.getName())
                .address(merchantHumo.getCity() + ", " + merchantHumo.getStreet()).build();
        when(merchantService.getMerchantByName(any())).thenReturn(Optional.of(merchantFromDb));
        Terminal newTerminal = getTerminal("ERA933507993768");
        newTerminal.setId(2L);
        when(terminalService.moveTerminalToAnotherMerchant(terminal.getId(), merchantFromDb.getId())).thenReturn(newTerminal);
        when(terminalService.patch(any())).thenReturn(terminal);
        when(terminalGroupService.findOrCreateGroupByName(any())).thenReturn(TerminalGroup.builder().id(1L).name("1111_ERA").build());

        assertEquals(MerchantHumo.OperationType.U, operationType);

        operationService.updateTypeOperation(terminalHumo, terminal, getHumoSettings().get(0), getAcquirer());

        verify(terminalService).patch(TerminalPatchDto.builder()
                .id(newTerminal.getId())
                .serialNumber(Optional.of(terminalHumo.getSerialNr()))
                .modelId(Optional.of(vendorModel.get().getId()))
                .defaultTerminalType(Optional.of(getHumoSettings().get(0).getTerminalType()))
                .servicePin(Optional.of(getHumoSettings().get(0).getServicePin()))
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(TerminalCreateDto.TerminalAcquirerParamsInputDto.builder()
                        .merchantName(merchantHumo.getMid() + "/" + merchantHumo.getTid())
                        .isoMerchantId(merchantHumo.getMid())
                        .isoTerminalId(merchantHumo.getTid())
                        .mcc(merchantHumo.getMcc())
                        .type(getHumoSettings().get(0).getTerminalType())
                        .acquirerId(getAcquirer().getId())
                        .availableOperations(getAcquirer().getOrderedAvailableOperations())
                        .paramsPaymentTypes(List.of()).build()), REPLACE))
                .address(Optional.of("TOSHKENT, BIRLASHGAN 2-TOR KO'CHASI 4-UY"))
                .groupId(Optional.of(1L))
                .build());
        verify(merchantService, times(0)).patch(MerchantPatchDto.builder()
                .id(merchantFromDb.getId())
                .address(Optional.of(merchantHumo.getCity() + ", " + merchantHumo.getStreet()))
                .name(Optional.of(merchantHumo.getName()))
                .build());
        verify(merchantService, times(0)).create(any());
        verify(terminalService, times(0)).create(any());
        verify(terminalService, times(0)).delete(any(Long.class));
    }


    @Test
    void updateTypeOperationWhenTerminalExistAndSameMerchant() {
        String fileName = "/TMS_U_00959_2108201136.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Terminal terminal = getTerminal("3507993768");
        Merchant merchant = terminal.getMerchant();
        merchant.setName("YTT \"ABDURAZAKOV ZOIR TOIROVICH\"");
        TerminalHumo terminalHumo = humoMap.get("ERA933507993768");
        MerchantHumo merchantHumo = terminalHumo.getMerchant();
        MerchantHumo.OperationType operationType = merchantHumo.getType();
        Optional<VendorModel> vendorModel = Optional.of(VendorModel.builder().id(1L).name("PAX_A930").vendor(Vendor.builder().id(1L).name("PAX").build()).build());

        when(terminalService.patch(any())).thenReturn(terminal);
        when(terminalGroupService.findOrCreateGroupByName(any())).thenReturn(TerminalGroup.builder().id(1L).name("1111_ERA").build());

        assertEquals(MerchantHumo.OperationType.U, operationType);


        operationService.updateTypeOperation(terminalHumo, terminal, getHumoSettings().get(0), getAcquirer());

        verify(terminalService).patch(TerminalPatchDto.builder()
                .id(terminal.getId())
                .serialNumber(Optional.of(terminalHumo.getSerialNr()))
                .modelId(Optional.of(vendorModel.get().getId()))
                .defaultTerminalType(Optional.of(getHumoSettings().get(0).getTerminalType()))
                .servicePin(Optional.of(getHumoSettings().get(0).getServicePin()))
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(TerminalCreateDto.TerminalAcquirerParamsInputDto.builder()
                        .merchantName(merchantHumo.getMid() + "/" + merchantHumo.getTid())
                        .isoMerchantId(merchantHumo.getMid())
                        .isoTerminalId(merchantHumo.getTid())
                        .mcc(merchantHumo.getMcc())
                        .type(getHumoSettings().get(0).getTerminalType())
                        .acquirerId(getAcquirer().getId())
                        .availableOperations(getAcquirer().getOrderedAvailableOperations())
                        .paramsPaymentTypes(List.of()).build()), REPLACE))
                .address(Optional.of("TOSHKENT, BIRLASHGAN 2-TOR KO'CHASI 4-UY"))
                .groupId(Optional.of(1L))
                .build());
        verify(merchantService, times(0)).patch(MerchantPatchDto.builder()
                .id(merchant.getId())
                .address(Optional.of(merchantHumo.getCity() + ", " + merchantHumo.getStreet()))
                .name(Optional.of(merchantHumo.getName()))
                .build());
        verify(merchantService, times(0)).create(any());
        verify(terminalService, times(0)).create(any());
        verify(terminalService, times(0)).delete(any(Long.class));
    }


    @Test
    void updateTypeOperationWhenTerminalAndHumoAcquirerExist() {
        String fileName = "/TMS_U_00959_2108201136.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Terminal terminal = getTerminal("3507993768");
        TerminalHumo terminalHumo = humoMap.get("ERA933507993768");
        terminalHumo.setSerialNr(terminalHumo.getSerialNr().substring(5));
        Set<TerminalAcquirerParams> acquirerParams = terminal.getTerminalAcquirerParams();

        Acquirer acquirer1 = Acquirer.builder().id(1L).name("Humo").build();
        Acquirer acquirer2 = Acquirer.builder().id(2L).name("sdflk").build();
        doReturn(Optional.of(acquirer1)).when(acquirerService).getByIdUsingCacheWithoutAccessControl(acquirer1.getId());
        doReturn(Optional.of(acquirer2)).when(acquirerService).getByIdUsingCacheWithoutAccessControl(acquirer2.getId());

        TerminalAcquirerParams params1 = TerminalAcquirerParams.builder()
                .merchantName("6666/5555")
                .isoTerminalId("5555").isoMerchantId("6666").pinMasterKeyKCV("8888").rkiEnabled(true)
                .acquirerId(acquirer1.getId())
                .mcc("0000")
                .type(TerminalType.ATTENDENT_11)
                .paramsPaymentTypes(List.of()).build();
        params1.setAvailableOperations(getTerminalAcquirerOperations(params1));
        acquirerParams.add(params1);

        TerminalAcquirerParams params2 = TerminalAcquirerParams.builder()
                .merchantName("456/123")
                .isoTerminalId("123").isoMerchantId("456").pinMasterKeyKCV("1234").rkiEnabled(true)
                .acquirerId(acquirer2.getId())
                .mcc("9999")
                .type(TerminalType.ATTENDENT_22)
                .paramsPaymentTypes(List.of())
                .build();
        params2.setAvailableOperations(getTerminalAcquirerOperations(params2));
        acquirerParams.add(params2);

        List<TerminalAcquirerParamsInputDto> paramsInputDtos = acquirerParams.stream()
                .map(this::createParamsInputDto)
                .toList();
        Optional<VendorModel> vendorModel = Optional.of(VendorModel.builder()
                .id(1L).name("A930")
                .vendor(Vendor.builder()
                        .id(1L)
                        .name("PAX")
                        .build())
                .build());

        MerchantHumo.OperationType operationType = terminalHumo.getMerchant().getType();

        when(merchantService.getMerchantByName(any())).thenReturn(Optional.empty());
        Merchant newMerchant = Merchant.builder()
                .id(2L)
                .name(terminalHumo.getMerchant().getName())
                .address(terminalHumo.getMerchant().getCity() + ", " + terminalHumo.getMerchant().getStreet()).build();
        when(merchantService.create(any())).thenReturn(newMerchant);
        when(terminalService.patch(any())).thenReturn(terminal);
        Terminal newTerminal = getTerminal("3507993768");
        when(terminalService.moveTerminalToAnotherMerchant(terminal.getId(), newMerchant.getId())).thenReturn(newTerminal);
        when(terminalGroupService.findOrCreateGroupByName(any())).thenReturn(TerminalGroup.builder().id(1L).name("1111_ERA").build());

        assertEquals(MerchantHumo.OperationType.U, operationType);

        operationService.updateTypeOperation(terminalHumo, terminal, getHumoSettings().get(0), getAcquirer());

        List<TerminalAcquirerParamsInputDto> paramsInputDtos1 = paramsInputDtos.stream()
                .peek(p -> {
                    if (p.getAcquirerId() == 1L) {
                        p.setMerchantName("009590482830501/01116769");
                        p.setIsoTerminalId("01116769");
                        p.setIsoMerchantId("009590482830501");
                        p.setMcc("5999");
                        p.setType(getHumoSettings().get(0).getTerminalType());
                        p.setParamsPaymentTypes(List.of());
                    }
                }).collect(Collectors.toList());

        verify(terminalService).patch(TerminalPatchDto.builder()
                .id(terminal.getId())
                .serialNumber(Optional.of(terminalHumo.getSerialNr()))
                .modelId(Optional.of(vendorModel.get().getId()))
                .defaultTerminalType(Optional.of(getHumoSettings().get(0).getTerminalType()))
                .servicePin(Optional.of(getHumoSettings().get(0).getServicePin()))
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(paramsInputDtos1, REPLACE))
                .address(Optional.of("TOSHKENT, BIRLASHGAN 2-TOR KO'CHASI 4-UY"))
                .groupId(Optional.of(1L))
                .build());
        verify(merchantService).create(MerchantCreateDto.builder()
                .name(terminalHumo.getMerchant().getName())
                .address(terminalHumo.getMerchant().getCity() + ", " + terminalHumo.getMerchant().getStreet())
                .build());
        verify(merchantService, times(1)).create(any());
        verify(terminalService, times(0)).create(any());
        verify(terminalService, times(0)).delete(any(Long.class));
    }

    @Test
    void operationIfTerminalNotFoundAndMerchantIsEmpty() {
        String fileName = "/TMS_I_01124_1210231428.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Terminal terminal = getTerminal("ERA93225153585");
        Merchant merchant = terminal.getMerchant();
        TerminalHumo terminalHumo = humoMap.get("ERA93225153585");
        Optional<VendorModel> vendorModel = Optional.of(VendorModel.builder().id(1L).name("PAX_A930").vendor(Vendor.builder().id(1L).name("PAX").build()).build());

        MerchantHumo.OperationType operationType = terminalHumo.getMerchant().getType();

        when(merchantService.getMerchantByName(any())).thenReturn(Optional.empty());
        when(merchantService.create(any())).thenReturn(getMerchant());
        when(terminalGroupService.findOrCreateGroupByName(any())).thenReturn(TerminalGroup.builder().id(1L).name(terminalHumo.getBankId()).build());

        assertEquals(MerchantHumo.OperationType.I, operationType);

        operationService.operationIfTerminalNotFound(terminalHumo, operationType, getHumoSettings().get(0), getAcquirer());

        verify(merchantService).getMerchantByName(any());
        verify(merchantService).create(MerchantCreateDto.builder()
                .address(terminalHumo.getMerchant().getCity() + ", " + terminalHumo.getMerchant().getStreet())
                .name(terminalHumo.getMerchant().getName()).build());
        verify(terminalService).create(TerminalCreateDto.builder()
                .serialNumber(terminalHumo.getSerialNr())
                .modelId(vendorModel.get().getId())
                .defaultTerminalType(getHumoSettings().get(0).getTerminalType())
                .servicePin(getHumoSettings().get(0).getServicePin())
                .country(CountryCode.UZ.getName())
                .merchantId(merchant.getId())
                .terminalAcquirerParams(List.of(TerminalCreateDto.TerminalAcquirerParamsInputDto.builder()
                        .merchantName(terminalHumo.getMerchant().getMid() + "/" + terminalHumo.getMerchant().getTid())
                        .isoTerminalId(terminalHumo.getMerchant().getTid())
                        .isoMerchantId(terminalHumo.getMerchant().getMid())
                        .mcc(terminalHumo.getMerchant().getMcc())
                        .type(getHumoSettings().get(0).getTerminalType())
                        .availableOperations(List.of(SALE))
                        .paramsPaymentTypes(List.of())
                        .acquirerId(getAcquirer().getId()).build()))
                .groupId(1L)
                .address("Toshkent shahar, Bektemir Bektemir")
                .build());

        verify(terminalService, times(0)).patch(any());
        verify(terminalService, times(0)).delete(any(Long.class));
    }

    @Test
    void runHumoHandlerForDeleteWhenTerminalExistAndParamsEqualsOne() {
        String fileName = "/TMS_D_00083_2108201352.xml";
        Map<String, TerminalHumo> humoMap = readFile(fileName);
        Terminal terminal = getTerminal("ERA932510142018");
        TerminalHumo terminalHumo = humoMap.get("ERA932510142018");

        Acquirer acquirer = getAcquirer();
        Set<TerminalAcquirerParams> terminalAcquirerParams = terminal.getTerminalAcquirerParams();
        terminalAcquirerParams.add(TerminalAcquirerParams.builder()
                .isoTerminalId("1234")
                .isoMerchantId("5678")
                .acquirerId(acquirer.getId()).build());

        assertEquals(MerchantHumo.OperationType.D, terminalHumo.getMerchant().getType());

        operationService.deleteTypeOperation(terminalHumo, terminal, getHumoSettings().get(0));

        verify(terminalService).delete(terminal.getId());
        verify(merchantService, times(0)).patch(any());
        verify(merchantService, times(0)).create(any());
        verify(merchantService, times(0)).delete(any(Long.class));
        verify(terminalService, times(0)).create(any());
        verify(terminalService, times(0)).patch(any());
    }

    public static Map<String, TerminalHumo> readFile(String filename) {
        File file = new File(Path.of("src/test/resources").toAbsolutePath() + filename);
        JAXBContext jaxbContext;
        Map<String, TerminalHumo> terminalMap = new HashMap<>();
        Terminals terminals = null;
        try {
            jaxbContext = JAXBContext.newInstance(Terminals.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            terminals = (Terminals) jaxbUnmarshaller.unmarshal(new FileReader(file));
            terminals.getTerminal().forEach(terminal -> {
                String sn = terminal.getSerialNr();
                if (!terminalMap.containsKey(sn)) {
                    terminalMap.put(sn, terminal);
                }
            });
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return terminalMap;
    }

    public static Terminal getTerminal(String sn) {
        return Terminal.builder()
                .id(1L)
                .country(CountryCode.UZ.getName())
                .merchant(getMerchant())
                .address("123")
                .serialNumber(sn)
                .build();
    }

    public static Merchant getMerchant() {
        return Merchant.builder()
                .id(1L)
                .name("merchant1")
                .address("addr1")
                .createdAt(OffsetDateTime.now())
                .lastArchivedAt(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .lastRestoredAt(OffsetDateTime.now())
                .archived(false)
                .createdBy(1L)
                .lastUpdatedAt(OffsetDateTime.now())
                .lastUpdatedBy(1L).build();
    }

    public static Acquirer getAcquirer() {
        Acquirer acquirer = Acquirer.builder()
                .id(1L)
                .name("Humo")
                .archived(false)
                .usingMacToSignRequests(false)
                .availableCurrencies(Set.of(CurrencyCode.USD))
                .cashbackLimit(1000L)
                .createdAt(OffsetDateTime.now())
                .createdBy(1L)
                .isoProtocol(IsoProtocol.TIETO_HUMO)
                .keySettings(new Acquirer.KeySettings())
                .paymentHost(new Acquirer.PaymentHost())
                .paymentSystemIds(Set.of(1L))
                .lastArchivedAt(OffsetDateTime.now())
                .lastRestoredAt(OffsetDateTime.now())
                .lastUpdatedAt(OffsetDateTime.now())
                .lastUpdatedBy(1L)
                .build();

        acquirer.setAvailableOperations(getAcquirerOperations(acquirer));

        return acquirer;
    }

    public static Set<AcquirerOperation> getAcquirerOperations(Acquirer acquirer) {
        return Set.of(
                AcquirerOperation.builder()
                        .id(1L)
                        .acquirer(acquirer)
                        .operation(SALE)
                        .operationOrder(1)
                        .createdAt(OffsetDateTime.now())
                        .createdBy(1L)
                        .createdAt(OffsetDateTime.now())
                        .lastUpdatedAt(OffsetDateTime.now())
                        .lastUpdatedBy(1L)
                        .build()
        );
    }

    public static List<TerminalAcquirerOperation> getTerminalAcquirerOperations(TerminalAcquirerParams terminalAcquirerParams) {
        AtomicLong idCounter = new AtomicLong(1);
        AtomicInteger orderCounter = new AtomicInteger(1);

        return Arrays.stream(AcquirerOperationType.values())
                .map(it -> TerminalAcquirerOperation.builder()
                        .id(idCounter.getAndIncrement())
                        .terminalAcquirerParams(terminalAcquirerParams)
                        .operation(it)
                        .operationOrder(orderCounter.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    private TerminalAcquirerParamsInputDto createParamsInputDto(TerminalAcquirerParams params) {
        return TerminalAcquirerParamsInputDto.builder()
                .merchantName(params.getMerchantName())
                .isoMerchantId(params.getIsoMerchantId())
                .isoTerminalId(params.getIsoTerminalId())
                .acquirerId(params.getAcquirerId())
                .pinMasterKeyKCV(params.getPinMasterKeyKCV())
                .rkiEnabled(params.isRkiEnabled())
                .type(params.getType())
                .mcc(params.getMcc())
                .availableOperations(params.getOrderedAvailableOperations())
                .paramsPaymentTypes(List.of())
                .build();
    }

    public static List<HumoSetting> getHumoSettings() {
        return List.of(HumoSetting.builder()
                        .humoId("ERA93")
                        .terminalType(TerminalType.ATTENDENT_11)
                        .servicePin("12345")
                        .modelId(1L)
                        .acquirerId(1L).build(),
                HumoSetting.builder()
                        .humoId("ERAAP")
                        .terminalType(TerminalType.ATTENDENT_11)
                        .servicePin("22222")
                        .modelId(1L)
                        .acquirerId(1L).build());
    }
}
