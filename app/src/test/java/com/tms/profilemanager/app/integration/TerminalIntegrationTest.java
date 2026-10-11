package com.tms.profilemanager.app.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.integration.rkl.dto.RKLResponseDto;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.CountryDto;
import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.LicenseDto;
import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.TerminalAcquirerParamsDto;
import com.tms.profilemanager.dto.TerminalDto;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.VendorModelDto;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto;
import com.tms.profilemanager.dto.create.TerminalCreateDto.TerminalAcquirerParamsInputDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.filter.AcquirerFilterDto;
import com.tms.profilemanager.dto.filter.TerminalFilterDto;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import com.tms.profilemanager.dto.patch.AcquirerPatchDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import com.tms.profilemanager.dto.patch.SystemUserPatchDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestDto;
import com.tms.profilemanager.dto.request.TerminalEncryptedKeysRequestV2Dto;
import com.tms.profilemanager.dto.request.TerminalSoftwareRequestDto;
import com.tms.profilemanager.dto.response.TerminalEncryptedKeysResponseDto;
import com.tms.profilemanager.dto.view.AcquirerView;
import com.tms.profilemanager.dto.view.PaymentSystemView;
import com.tms.profilemanager.dto.view.TerminalAcquirerParamsView;
import com.tms.profilemanager.dto.view.TerminalView;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.tms.profilemanager.dto.enums.AcquirerOperationType.CANCEL;
import static com.tms.profilemanager.dto.enums.AcquirerOperationType.PAYMENT;
import static com.tms.profilemanager.dto.enums.AcquirerOperationType.SALE;
import static com.tms.profilemanager.dto.enums.TerminalType.ATTENDENT_11;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.ADD;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REMOVE;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REPLACE;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TerminalIntegrationTest extends AbstractIntegrationTest {

    @Value("${security.system-user.default-terminal-password}")
    String defaultTerminalPassword;

    private Long preparedMerchantId;
    private Long preparedPaymentSystemId;
    private Long preparedAcquirerId;
    private Long preparedTerminalGroupId;
    private Long preparedContactLimitId;
    private Long preparedContactlessLimitId;
    private Long preparedGeneralLimitId;
    private String preparedPaymentTypeValue;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedMerchantId = merchantGenerator.createAndSave(1).getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        preparedAcquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId)).getId();
        preparedTerminalGroupId = terminalGroupGenerator.createAndSave(1).getId();
        preparedPaymentTypeValue = paymentTypeGenerator.createAndSave(1).getPaymentValue();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto dto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        assertEquals(dto, terminalTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        Long anotherAcquirerId = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId)).getId();
        IntStream.range(1, 4).forEach(it ->
                terminalGenerator.createAndSave(it, preparedMerchantId, Set.of(preparedAcquirerId, anotherAcquirerId), vendorModelId, preparedPaymentTypeValue));

        TerminalFilterDto filterDto = new TerminalFilterDto();
        List<Long> ids = terminalTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, terminalTestClient.search(filterDto).getTotalElements());

        Long newMerchantId = merchantGenerator.createAndSave(2).getId();
        terminalGenerator.createAndSave(5, newMerchantId, Set.of(preparedAcquirerId, anotherAcquirerId), vendorModelId, preparedPaymentTypeValue);
        TerminalFilterDto newFilterDto = new TerminalFilterDto();
        List<Long> idsBeforeFiltration = terminalTestClient.search(newFilterDto).stream().map(BaseDto::getId).toList();
        assertEquals(4, idsBeforeFiltration.size());

        newFilterDto.setMerchantIds(Set.of(newMerchantId));
        List<Long> IdsAfterFiltration = terminalTestClient.search(newFilterDto).stream().map(BaseDto::getId).toList();
        assertEquals(1, IdsAfterFiltration.size());
    }

    @Test
    void searchBySnOrTidOrMidTest() {
        VendorDto vendorDto = vendorTestClient.search(new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto terminalDto0 = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        TerminalDto terminalDto1 = terminalGenerator.createAndSave(2, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        TerminalDto terminalDto2 = terminalGenerator.createAndSave(3, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);

        TerminalFilterDto filterDto = new TerminalFilterDto();
        List<TerminalDto> terminals = terminalTestClient.search(filterDto).stream().toList();
        assertEquals(3, terminals.size());

        terminalTestClient.patch(TerminalPatchDto.builder().id(terminalDto0.getId()).serialNumber(Optional.of("12345678")).build());
        terminalTestClient.patch(TerminalPatchDto.builder().id(terminalDto1.getId())
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder()
                        .merchantName("123123/12345678")
                        .mcc("1234")
                        .type(ATTENDENT_11)
                        .acquirerId(preparedAcquirerId)
                        .isoTerminalId("12345678")
                        .isoMerchantId("123123")
                        .availableOperations(List.of(SALE))
                        .build()), REPLACE)).build());
        terminalTestClient.patch(TerminalPatchDto.builder().id(terminalDto2.getId())
                .terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder()
                        .merchantName("12345678/232423")
                        .mcc("1234")
                        .type(ATTENDENT_11)
                        .acquirerId(preparedAcquirerId)
                        .isoTerminalId("232423")
                        .isoMerchantId("12345678")
                        .availableOperations(List.of(SALE))
                        .build()), REPLACE)).build());

        TerminalFilterDto subFilterDto1 = new TerminalFilterDto();
        subFilterDto1.setSearchSerialNumber("12345678");
        TerminalFilterDto subFilterDto2 = new TerminalFilterDto();
        subFilterDto2.setSearchTid("12345678");
        TerminalFilterDto subFilterDto3 = new TerminalFilterDto();
        subFilterDto3.setSearchMid("12345678");
        filterDto.setSubFiltersAggregatedByOr(Set.of(subFilterDto1, subFilterDto2, subFilterDto3));
        List<TerminalDto> content = terminalTestClient.search(filterDto).getContent();

        assertEquals(3, content.size());
        assertEquals(1, content.stream().filter(terminalDto -> terminalDto.getSerialNumber().equals("12345678")).toList().size());
        assertEquals(1, content.stream().filter(terminalDto -> terminalDto.getTerminalAcquirerParams().stream().toList().get(0).getIsoTerminalId().equals("12345678")).toList().size());
        assertEquals(1, content.stream().filter(terminalDto -> terminalDto.getTerminalAcquirerParams().stream().toList().get(0).getIsoMerchantId().equals("12345678")).toList().size());
    }

    @Test
    void createTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalCreateDto createDto = terminalGenerator.buildCreateDto(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);

        createAndAssertBadRequest(createDto.toBuilder().serialNumber(null).build());
        createAndAssertBadRequest(createDto.toBuilder().serialNumber("").build());
        createAndAssertBadRequest(createDto.toBuilder().serialNumber(repeat("a", 101)).build());

        createAndAssertBadRequest(createDto.toBuilder().modelId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().modelId(-1L).build());

        createAndAssertBadRequest(createDto.toBuilder().defaultTerminalType(null).build());

        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(null).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId(null).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId("").build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId(repeat("a", 9)).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId("23jkn23n").build())).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId(null).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId("").build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId(repeat("a", 16)).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId("wkjhdhk029").build())).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().pinMasterKeyKCV(null).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().pinMasterKeyKCV("").build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams
                (List.of(TerminalAcquirerParamsInputDto.builder().pinMasterKeyKCV(repeat("a", 16)).build())).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().availableOperations(List.of()).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().availableOperations(List.of(SALE, CANCEL)).build())).build());
        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().availableOperations(List.of(SALE)).build())).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().mcc(null).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().mcc(repeat("0", 1)).build())).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().mcc(repeat("a", 4)).build())).build());
        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().mcc(repeat("0", 4)).build())).build());

        createAndAssertBadRequest(createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().type(null).build())).build());
        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().type(ATTENDENT_11).build())).build());

        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().paramsPaymentTypes(null).build())).build());
        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().paramsPaymentTypes(List.of()).build())).build());
        assertDoesNotThrow(() -> createDto.toBuilder().terminalAcquirerParams(
                List.of(TerminalAcquirerParamsInputDto.builder().paramsPaymentTypes(List.of(preparedPaymentTypeValue, preparedPaymentTypeValue)).build())).build());

        createAndAssertBadRequest(createDto.toBuilder().merchantId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().merchantId(-1L).build());

        createAndAssertBadRequest(createDto.toBuilder().servicePin("12345678901").build());
        createAndAssertBadRequest(createDto.toBuilder().servicePin("abc123").build());

        createAndAssertBadRequest(createDto.toBuilder().groupId(-1L).build());

        createAndAssertBadRequest(createDto.toBuilder().address(null).build());
        assertDoesNotThrow(() -> createDto.toBuilder().address("").build());

        createDto.setAddress("1234");
        TerminalDto dto = terminalTestClient.create(createDto);
        assertEquals(1, dto.getTerminalAcquirerParams().stream().map(TerminalAcquirerParamsDto::getParamsPaymentTypes).toList().size());
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, terminalTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto dto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        TerminalPatchDto patchDto = TerminalPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().serialNumber(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().serialNumber(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().serialNumber(Optional.of(repeat("a", 101))).build());
        patchAndAssertOk(dto, repeat("a", 100), dto::setSerialNumber, p -> p::setSerialNumber);

        patchAndAssertBadRequest(patchDto.toBuilder().modelId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().modelId(Optional.of(-1L)).build());

        patchAndAssertBadRequest(patchDto.toBuilder().defaultTerminalType(Optional.empty()).build());
        patchAndAssertOk(dto, TerminalType.UNATTENDENT_14, v -> dto.setDefaultTerminalType(TerminalType.Dto.of(v)), p -> p::setDefaultTerminalType);
        patchAndAssertOk(dto, TerminalType.ATTENDENT_22, v -> dto.setDefaultTerminalType(TerminalType.Dto.of(v)), p -> p::setDefaultTerminalType);

        assertDoesNotThrow(() -> patchDto.toBuilder().terminalAcquirerParams(null).build());

        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId(null).build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId("").build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().isoTerminalId(repeat("a", 9)).build()),REPLACE)).build());

        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId(null).build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId("").build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().isoMerchantId(repeat("a", 16)).build()), REPLACE)).build());

        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().pinMasterKeyKCV(null).build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().pinMasterKeyKCV("").build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().pinMasterKeyKCV(repeat("a", 16)).build()), REPLACE)).build());

        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().availableOperations(List.of()).build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().availableOperations(List.of(SALE, CANCEL)).build()), REPLACE)).build());
        assertDoesNotThrow(() -> patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().availableOperations(List.of(SALE)).build()), REPLACE)).build());

        assertDoesNotThrow(() -> patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().paramsPaymentTypes(null).build()), REPLACE)).build());
        assertDoesNotThrow(() -> patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().paramsPaymentTypes(List.of()).build()), REPLACE)).build());
        assertDoesNotThrow(() -> patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().paramsPaymentTypes(List.of(preparedPaymentTypeValue, preparedPaymentTypeValue)).build()), REPLACE)).build());

        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().mcc(null).build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().mcc("").build()), REPLACE)).build());
        patchAndAssertBadRequest(patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().mcc(repeat("a", 4)).build()), REPLACE)).build());
        assertDoesNotThrow(() -> patchDto.toBuilder().terminalAcquirerParams(
                new BasePatchDto.ListPatch<>(List.of(TerminalAcquirerParamsInputDto.builder().mcc(repeat("1", 4)).build()), REPLACE)).build());

        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of("abc121")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of("12345678901")).build());
        patchAndAssertOk(dto, "123456", dto::setServicePin, p -> p::setServicePin);

        patchAndAssertBadRequest(patchDto.toBuilder().address(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().address(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().address(Optional.of(" ")).build());
        patchAndAssertOk(dto, "123", dto::setAddress, p -> p::setAddress);

        Long preparedAcquirerId2 = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId)).getId();
        acquirerTestClient.patch(AcquirerPatchDto.builder().id(preparedAcquirerId).availableOperations(List.of(SALE, PAYMENT)).build());
        var paramsInputDto = terminalGenerator.buildParamsInputDto(2, preparedAcquirerId2, preparedPaymentTypeValue);
        assertEquals(1, dto.getTerminalAcquirerParams().size());
        paramsInputDto.setRkiEnabled(!dto.getTerminalAcquirerParams().iterator().next().isRkiEnabled());
        TerminalDto patchedDto = terminalTestClient.patch(patchDto.toBuilder().id(dto.getId()).terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(paramsInputDto), ADD)).build());
        assertEquals(1, patchedDto.getTerminalAcquirerParams().stream().filter(it -> it.getAcquirerId().equals(preparedAcquirerId)).map(TerminalAcquirerParamsDto::getParamsPaymentTypes).toList().size());
        assertEquals(2, patchedDto.getTerminalAcquirerParams().size());
        assertInputDtoMatchesDto(paramsInputDto, IterableUtils.find(patchedDto.getTerminalAcquirerParams(), it -> it.getAcquirerId().equals(preparedAcquirerId2)));

        terminalTestClient.patch(patchDto.toBuilder().terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(paramsInputDto), REMOVE)).build());
        patchedDto = terminalTestClient.getById(dto.getId());
        assertEquals(1, patchedDto.getTerminalAcquirerParams().size());
        assertNotEquals(preparedAcquirerId2, patchedDto.getTerminalAcquirerParams().iterator().next().getAcquirerId());

        terminalTestClient.patch(patchDto.toBuilder().terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(paramsInputDto), REPLACE)).build());
        patchedDto = terminalTestClient.getById(dto.getId());
        assertEquals(1, patchedDto.getTerminalAcquirerParams().size());
        assertInputDtoMatchesDto(paramsInputDto, patchedDto.getTerminalAcquirerParams().iterator().next());

        paramsInputDto.setIsoMerchantId("1223GHH");
        terminalTestClient.patch(patchDto.toBuilder().terminalAcquirerParams(new BasePatchDto.ListPatch<>(List.of(paramsInputDto), REPLACE)).build());
        patchedDto = terminalTestClient.getById(dto.getId());
        assertEquals(1, patchedDto.getTerminalAcquirerParams().size());
        assertInputDtoMatchesDto(paramsInputDto, patchedDto.getTerminalAcquirerParams().iterator().next());
    }

    @Test
    void patchGroupTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto dto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);

        assertEquals(dto, terminalTestClient.getById(dto.getId()));

        TerminalGroupDto terminalGroupDto = terminalGroupTestClient.getById(preparedTerminalGroupId);
        assertNull(dto.getGroup());
        TerminalDto patchDto = terminalTestClient.patch(TerminalPatchDto.builder().id(dto.getId()).groupId(ofNullable(terminalGroupDto.getId())).build());
        assertNotEquals(dto, terminalTestClient.getById(dto.getId()));
        assertNotNull(patchDto.getGroup());

        TerminalDto terminalAfterPatching = terminalTestClient.getById(dto.getId());
        assertEquals(terminalAfterPatching, patchDto);

        terminalTestClient.patch(TerminalPatchDto.builder().id(dto.getId()).groupId(Optional.empty()).build());
        assertNull(terminalTestClient.getById(dto.getId()).getGroup());
    }

    @Test
    void moveTerminalToAnotherMerchantTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto dto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        MerchantDto anotherMerchantDto = merchantGenerator.createAndSave(2);

        TerminalDto movedTerminalDto = terminalTestClient.moveTerminalToAnotherMerchant(dto.getId(), anotherMerchantDto.getId());
        dto = terminalTestClient.getById(dto.getId());

        assertNotEquals(dto.getId(), movedTerminalDto.getId());
        assertTrue(dto.getArchived());
        assertFalse(movedTerminalDto.getArchived());
        assertEquals(movedTerminalDto.getMerchant(), anotherMerchantDto);
        assertEquals(dto.getDefaultTerminalType(), movedTerminalDto.getDefaultTerminalType());
        assertEquals(dto.getModel(), movedTerminalDto.getModel());
        assertEquals(dto.getTerminalAcquirerParams().iterator().next().getIsoTerminalId(),
                movedTerminalDto.getTerminalAcquirerParams().iterator().next().getIsoTerminalId());
        assertEquals(dto.getTerminalAcquirerParams().iterator().next().getIsoMerchantId(),
                movedTerminalDto.getTerminalAcquirerParams().iterator().next().getIsoMerchantId());
        assertEquals(dto.getCountry(), movedTerminalDto.getCountry());
        assertEquals(dto.getSerialNumber(), movedTerminalDto.getSerialNumber());
        assertEquals(dto.getServicePin(), movedTerminalDto.getServicePin());
        Set<Long> expectedAcquirersIds = acquirerTestClient.search(AcquirerFilterDto.builder().terminalsIds(Set.of(dto.getId())).build())
                .stream().map(BaseDto::getId).collect(toSet());
        Set<Long> actualAcquirersIds = acquirerTestClient.search(AcquirerFilterDto.builder().terminalsIds(Set.of(movedTerminalDto.getId())).build())
                .stream().map(BaseDto::getId).collect(toSet());
        assertEquals(expectedAcquirersIds, actualAcquirersIds);

        movedTerminalDto = terminalTestClient.patch(TerminalPatchDto.builder()
                .id(movedTerminalDto.getId())
                .servicePin(Optional.of("123456"))
                .build());

        TerminalDto movedTerminalDto2 = terminalTestClient.moveTerminalToAnotherMerchant(movedTerminalDto.getId(), preparedMerchantId);
        movedTerminalDto = terminalTestClient.getById(movedTerminalDto.getId());

        assertEquals(dto.getId(), movedTerminalDto2.getId());
        assertFalse(movedTerminalDto2.getArchived());
        assertTrue(movedTerminalDto.getArchived());
        assertEquals(preparedMerchantId, movedTerminalDto2.getMerchant().getId());
        assertEquals(anotherMerchantDto.getId(), movedTerminalDto.getMerchant().getId());
        assertEquals(dto.getServicePin(), movedTerminalDto2.getServicePin());
        assertNotEquals(movedTerminalDto.getServicePin(), movedTerminalDto2.getServicePin());
    }

    @Test
    void deleteByIdTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto dto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        assertNotNull(terminalTestClient.getById(dto.getId()).getId());
        terminalTestClient.deleteById(dto.getId());
        terminalTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto dto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        assertFalse(terminalTestClient.getById(dto.getId()).getArchived());

        terminalTestClient.archiveById(dto.getId());
        assertTrue(terminalTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(terminalService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        terminalTestClient.restoreById(dto.getId());
        assertFalse(terminalTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(terminalService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    @Test
    void changeTerminalSerialNumberTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto terminalDto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        SystemUserDto systemUserDto = systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).orElseThrow();
        assertEquals(terminalDto.getSerialNumber(), systemUserDto.getTerminalSerialNumber());

        String newSerialNumber = "new_serial_number_123";
        TerminalPatchDto patchDto = TerminalPatchDto.builder()
                .id(terminalDto.getId())
                .serialNumber(Optional.of(newSerialNumber))
                .build();
        terminalTestClient.patch(patchDto);

        assertTrue(systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).isEmpty());

        systemUserDto = systemUserTestClient.getByTerminalSerialNumber(newSerialNumber).orElseThrow();
        assertEquals(newSerialNumber, systemUserDto.getTerminalSerialNumber());
        assertEquals(newSerialNumber, systemUserDto.getUsername());
    }

    @Test
    void getTerminalEncryptedSecretKeyTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto terminalDto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        SystemUserDto systemUserDto = systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).orElseThrow();
        assertNotNull(systemUserDto.getId());
        assertEquals(terminalDto.getSerialNumber(), systemUserDto.getTerminalSerialNumber());

        requestSender.setCurrentUser(systemUserDto.getUsername(), defaultTerminalPassword);

        final String cypherKey = "cypher-key";
        final String encryptedKey = "encrypted-key";
        final String pinMasterKeyKCV = "ABC123";
        assertEquals(1, terminalDto.getTerminalAcquirerParams().size());
        assertNotEquals(pinMasterKeyKCV, terminalDto.getTerminalAcquirerParams().iterator().next().getPinMasterKeyKCV());

        TerminalEncryptedKeysRequestDto request = TerminalEncryptedKeysRequestDto.builder()
                .algorithm("rsa")
                .cypherKey(cypherKey)
                .acquirersIds(Set.of(preparedAcquirerId))
                .build();
        RKLResponseDto rklResponseDto = RKLResponseDto.builder()
                .encryptedKeys(Set.of(RKLResponseDto.RKLEncryptedKeyDto.builder()
                        .acquirerId(terminalDto.getTerminalAcquirerParams().iterator().next().getAcquirerId())
                        .encryptedKey(encryptedKey)
                        .pinMasterKeyKCV(pinMasterKeyKCV)
                        .tid("1")
                        .mid("1")
                        .build()))
                .build();
        when(rklClient.getRKLResponse(any(Terminal.class), eq(request))).thenReturn(rklResponseDto);

        assertEquals(terminalDto.getId(), terminalTestClient.getTerminalLinkedWithCurrentUser().getId());
        TerminalEncryptedKeysResponseDto response = terminalTestClient.getTerminalEncryptedSecretKeys(request);

        assertEquals(rklResponseDto.getEncryptedKeys().size(), response.getEncryptedKeys().size());
        rklResponseDto.getEncryptedKeys().forEach(expected -> assertTrue(response.getEncryptedKeys().stream().anyMatch(actual ->
                Objects.equals(expected.getEncryptedKey(), actual.getEncryptedKey())
                        && Objects.equals(expected.getAcquirerId(), actual.getAcquirerId())
                        && Objects.equals(expected.getPinMasterKeyKCV(), actual.getPinMasterKeyKCV())
                        && Objects.equals(expected.getKeyScheme(), actual.getKeyScheme()))));

        TerminalDto changedTerminalDto = terminalTestClient.getById(terminalDto.getId());
        assertEquals(1, changedTerminalDto.getTerminalAcquirerParams().size());
        assertNotNull(changedTerminalDto.getTerminalAcquirerParams().stream()
                .filter(it -> it.getAcquirerId().equals(terminalDto.getTerminalAcquirerParams().iterator().next().getAcquirerId()))
                .findFirst().get());
        assertEquals(pinMasterKeyKCV, changedTerminalDto.getTerminalAcquirerParams().iterator().next().getPinMasterKeyKCV());
    }

    @Test
    void getTerminalEncryptedSecretKeyV2Test() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto terminalDto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        SystemUserDto systemUserDto = systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).orElseThrow();
        assertNotNull(systemUserDto.getId());
        assertEquals(terminalDto.getSerialNumber(), systemUserDto.getTerminalSerialNumber());

        requestSender.setCurrentUser(systemUserDto.getUsername(), defaultTerminalPassword);

        final String cypherKey = "cypher-key";
        final String encryptedKey = "encrypted-key";
        final String pinMasterKeyKCV = "ABC123";
        assertEquals(1, terminalDto.getTerminalAcquirerParams().size());
        assertNotEquals(pinMasterKeyKCV, terminalDto.getTerminalAcquirerParams().iterator().next().getPinMasterKeyKCV());

        TerminalEncryptedKeysRequestV2Dto request = TerminalEncryptedKeysRequestV2Dto.builder()
                .algorithm("rsa")
                .cypherKey(cypherKey)
                .acquirers(Set.of(TerminalEncryptedKeysRequestV2Dto.AcquirerTerminalKey.builder()
                                .acquirerId(preparedAcquirerId)
                                .mid("1")
                                .tid("1")
                        .build()))
                .build();
        RKLResponseDto rklResponseDto = RKLResponseDto.builder()
                .encryptedKeys(Set.of(RKLResponseDto.RKLEncryptedKeyDto.builder()
                        .acquirerId(terminalDto.getTerminalAcquirerParams().iterator().next().getAcquirerId())
                        .mid("1")
                        .tid("1")
                        .encryptedKey(encryptedKey)
                        .pinMasterKeyKCV(pinMasterKeyKCV)
                        .build()))
                .build();
        when(rklClient.getRKLResponseV2(any(Terminal.class), eq(request))).thenReturn(rklResponseDto);

        assertEquals(terminalDto.getId(), terminalTestClient.getTerminalLinkedWithCurrentUser().getId());
        TerminalEncryptedKeysResponseDto response = terminalTestClient.getTerminalEncryptedSecretKeysV2(request);

        assertEquals(rklResponseDto.getEncryptedKeys().size(), response.getEncryptedKeys().size());
        rklResponseDto.getEncryptedKeys().forEach(expected -> assertTrue(response.getEncryptedKeys().stream().anyMatch(actual ->
                Objects.equals(expected.getEncryptedKey(), actual.getEncryptedKey())
                        && Objects.equals(expected.getAcquirerId(), actual.getAcquirerId())
                        && Objects.equals(expected.getPinMasterKeyKCV(), actual.getPinMasterKeyKCV())
                        && Objects.equals(expected.getKeyScheme(), actual.getKeyScheme()))));

        TerminalDto changedTerminalDto = terminalTestClient.getById(terminalDto.getId());
        assertEquals(1, changedTerminalDto.getTerminalAcquirerParams().size());
        assertNotNull(changedTerminalDto.getTerminalAcquirerParams().stream()
                .filter(it -> it.getAcquirerId().equals(terminalDto.getTerminalAcquirerParams().iterator().next().getAcquirerId()))
                .findFirst().get());
        assertEquals(pinMasterKeyKCV, changedTerminalDto.getTerminalAcquirerParams().iterator().next().getPinMasterKeyKCV());
    }

    @Test
    void countForMerchantsTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        Long preparedMerchant2Id = merchantGenerator.createAndSave(2).getId();
        terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        terminalGenerator.createAndSave(2, preparedMerchant2Id, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        terminalGenerator.createAndSave(3, preparedMerchant2Id, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);
        Map<Long, Long> resultMap = terminalTestClient.getCountForMerchants(Set.of(preparedMerchantId, preparedMerchant2Id));
        assertEquals(2, resultMap.size());
        assertEquals(1, resultMap.get(preparedMerchantId));
        assertEquals(2, resultMap.get(preparedMerchant2Id));
    }

    @Test
    void setTerminalSoftwareInfoTest() {
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().get().getId();
        TerminalDto terminalDto = terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelId, preparedPaymentTypeValue);

        SystemUserDto systemUserDto = systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).orElseThrow();
        requestSender.setCurrentUser(systemUserDto.getUsername(), defaultTerminalPassword);
        terminalTestClient.setTerminalSoftwareInfo(TerminalSoftwareRequestDto.builder()
                .softwareVersion("1.0.0")
                .softwareName("Era pos")
                .mode("att")
                .softwareVendor("PAX")
                .softwareModel("A910")
                .build());

        requestSender.setCurrentUser(ADMIN);
        TerminalDto dto = terminalTestClient.getById(terminalDto.getId());
        assertNotNull(dto.getSoftwareVersion());
        assertNotNull(dto.getSoftwareName());
        assertNotNull(dto.getMode());
        assertNotNull(dto.getSoftwareVendor());
        assertNotNull(dto.getSoftwareModel());
    }

    @Test
    void getTerminalExtendedIndoCircuitBreakerTest() {
        Long vendorModelId = vendorTestClient.search(new VendorFilterDto()).getContent().stream()
                .flatMap(vendor -> vendor.getModels().stream())
                .findFirst().orElseThrow().getId();
        TerminalDto terminalDto = terminalGenerator.createAndSave(2, preparedMerchantId, Set.of(), vendorModelId, preparedPaymentTypeValue);

        SystemUserDto systemUserDto = systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).orElseThrow();
        requestSender.setCurrentUser(systemUserDto.getUsername(), defaultTerminalPassword);

        Mockito.doThrow(QueryTimeoutException.class).when(terminalService).getTerminalLinkedToCurrentSystemUser();

        MvcResult mvcResult = terminalTestClient.getMvcResultForTerminalViewLinkedWithCurrentUser();
        assertEquals(500, mvcResult.getResponse().getStatus()); // check that endpoint returns 500 error

        // wait until circuit breaker is opened
        for (int i = 0; i < 10; i++) {
            mvcResult = terminalTestClient.getMvcResultForTerminalViewLinkedWithCurrentUser();
            if (mvcResult.getResponse().getStatus() == 503) {
                break; // circuit breaker is opened
            }
            if (i == 9) { // 11th request
                throw new IllegalStateException("Circuit breaker was not opened after 11 requests");
            }
        }

        // wait until circuit breaker is closed (about 1 second)
        Awaitility.await()
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> terminalTestClient.getMvcResultForTerminalViewLinkedWithCurrentUser().getResponse().getStatus() == 500);

        Mockito.reset(terminalService);
    }

    @Test
    void getTerminalExtendedInfoTest() {
        ContactlessLimitDto contactlessLimitDto = contactlessLimitGenerator.createAndSave(2, Collections.emptyList());
        ContactLimitDto contactLimitDto = contactLimitGenerator.createAndSave(2);
        GeneralLimitDto generalLimitDto = generalLimitGenerator.createAndSave(2, contactLimitDto.getId(), contactlessLimitDto.getId());
        PaymentSystemDto paymentSystemDto = paymentSystemGenerator.createAndSave(2, generalLimitDto.getId());
        EMVAppDto emvApp = emvAppGenerator.createAndSave(2, paymentSystemDto.getId(), ATTENDENT_11);
        emvAppGenerator.createAndSave(3, paymentSystemDto.getId(), TerminalType.ATTENDENT_12);
        emvAppGenerator.createAndSave(4, paymentSystemDto.getId(), null);
        ContactlessAppDto contactlessApp = contactlessAppGenerator.createAndSave(2, paymentSystemDto.getId(), ATTENDENT_11);
        contactlessAppGenerator.createAndSave(3, paymentSystemDto.getId(), TerminalType.ATTENDENT_12);
        contactlessAppGenerator.createAndSave(4, paymentSystemDto.getId(), null);

        AcquirerDto acquirerDto = acquirerGenerator.createAndSave(2, Set.of(paymentSystemDto.getId()));
        VendorDto vendorDto = vendorTestClient.search( new VendorFilterDto()).getContent().get(0);
        Long vendorModelId = vendorDto.getModels().stream().findFirst().orElseThrow().getId();

        TerminalDto terminalDto = terminalGenerator.createAndSave(2, preparedMerchantId, Set.of(acquirerDto.getId()), vendorModelId, preparedPaymentTypeValue);
        LicenseDto dto = licenseTestClient.create(LicenseCreateDto.builder().licenseCode("123").serialNumber(terminalDto.getSerialNumber()).build());
        SystemUserDto systemUserDto = systemUserTestClient.getByTerminalSerialNumber(terminalDto.getSerialNumber()).orElseThrow();
        assertNotNull(systemUserDto.getId());
        assertEquals(terminalDto.getSerialNumber(), systemUserDto.getTerminalSerialNumber());
        Long preparedRoleId = roleGenerator.createAndSave(1, Set.of(Permission.values())).getId();
        systemUserTestClient.patch(SystemUserPatchDto.builder().id(systemUserDto.getId()).roleId(Optional.of(preparedRoleId)).build());

        requestSender.setCurrentUser(systemUserDto.getUsername(), defaultTerminalPassword);

        TerminalView terminalView = terminalTestClient.getTerminalViewLinkedWithCurrentUser();

        OffsetDateTime beforeConfigUpdatedDate = terminalTestClient.getById(terminalView.getId()).getConfigUpdatedDate();
        assertNull(terminalView.getConfigUpdatedDate());
        assertNotNull(terminalTestClient.getById(terminalView.getId()).getConfigUpdatedDate());
        assertEquals(2, terminalView.getAcquirers().stream()
                .findFirst().get().getAcquirer().getPaymentSystems().stream()
                .findFirst().get().getEmvApps().stream()
                .filter(emvAppDto -> ATTENDENT_11.equals(emvAppDto.getTerminalType()) ||
                        emvAppDto.getTerminalType() == null)
                .collect(toSet()).size());
        assertEquals(2, terminalView.getAcquirers().stream()
                .findFirst().get().getAcquirer().getPaymentSystems().stream()
                .findFirst().get().getContactlessApps().stream()
                .filter(contactlessAppDto -> ATTENDENT_11.equals(contactlessAppDto.getTerminalType()) ||
                        contactlessAppDto.getTerminalType() == null)
                .collect(toSet()).size());

        assertEquals(terminalDto.getId(), terminalView.getId());
        assertNull(terminalView.getCreatedAt());
        assertNull(terminalView.getArchived());
        assertNull(terminalView.getTerminalAcquirerParams());
        assertEquals(1, terminalView.getAcquirers().size());
        TerminalAcquirerParamsView acquirerParamsView = terminalView.getAcquirers().iterator().next();
        assertNotNull(acquirerParamsView.getIsoTerminalId());
        assertNotNull(acquirerParamsView.getIsoMerchantId());
        assertNotNull(acquirerParamsView.getPinMasterKeyKCV());
        AcquirerView acquirerView = acquirerParamsView.getAcquirer();
        assertEquals(acquirerDto.getId(), acquirerView.getId());
        assertNull(acquirerView.getCreatedAt());
        assertNull(acquirerView.getArchived());
        assertNull(acquirerView.getPaymentSystemsShortInfo());
        assertEquals(1, acquirerView.getPaymentSystems().size());
        PaymentSystemView paymentSystemView = acquirerView.getPaymentSystems().iterator().next();
        assertEquals(paymentSystemDto.getId(), paymentSystemView.getId());
        assertNull(paymentSystemView.getCreatedAt());
        assertNull(paymentSystemView.getArchived());
        assertEquals(dto.getLicenseCode(), terminalView.getLicense());

        terminalTestClient.patch(TerminalPatchDto.builder().id(terminalDto.getId()).groupId(ofNullable(preparedTerminalGroupId)).build());
        modificationTerminalGenerator.createAndSave(preparedTerminalGroupId);
        modificationAcquirerGenerator.createAndSave(acquirerDto.getId(), preparedTerminalGroupId);
        modificationPaymentSystemGenerator.createAndSave(1, paymentSystemDto.getId(), preparedTerminalGroupId, Set.of(preparedGeneralLimitId)).getId();modificationEmvAppGenerator.createAndSave(paymentSystemDto.getId(), preparedTerminalGroupId, emvApp.getId()).getId();
        modificationContactlessAppGenerator.createAndSave(paymentSystemDto.getId(), preparedTerminalGroupId, contactlessApp.getId()).getId();

        assertEquals("12345", terminalView.getServicePin());
        assertEquals("Uzbekistan", terminalView.getCountry().getName());
        assertNull(terminalView.getAcquirers().stream().findFirst().get().getAcquirer().getCashbackLimit());
        assertEquals(1 , terminalView.getAcquirers().stream().findFirst().get().getAcquirer().getKeySettings().getPinMasterKeyIndex());
        assertEquals("1.1.1.1" , terminalView.getAcquirers().stream().findFirst().get().getAcquirer().getPaymentHost().getHost());
        PaymentSystemView paymentSystemViewBeforeModification = terminalView.getAcquirers().stream().findFirst().get().getAcquirer().getPaymentSystems().stream().findFirst().get();
        assertEquals("2" ,  paymentSystemViewBeforeModification.getContactlessAvn());
        assertEquals(2L ,  paymentSystemViewBeforeModification.getDefaultLimits().getContactLimit().getFloorLimit());
        assertEquals(2 ,  paymentSystemViewBeforeModification.getDefaultLimits().getContactLimit().getPinLimit());
        assertEquals(2L ,  paymentSystemViewBeforeModification.getDefaultLimits().getContactlessLimit().getFloorLimit());
        assertEquals(2L ,  paymentSystemViewBeforeModification.getDefaultLimits().getContactlessLimit().getNoDeviceCvmLimit());
        assertEquals("2" ,  paymentSystemViewBeforeModification.getContactlessAvn());
        assertEquals("aid2", paymentSystemViewBeforeModification.getContactlessApps().stream().filter(it -> it.getId().equals(contactlessApp.getId())).findFirst().get().getAid());
        assertEquals("aid2", paymentSystemViewBeforeModification.getEmvApps().stream().filter(it ->it.getId().equals(emvApp.getId())).findFirst().get().getAid());

        TerminalView terminalViewAfterModification = terminalTestClient.getTerminalViewLinkedWithCurrentUser();
        OffsetDateTime afterConfigUpdatedDate = terminalTestClient.getById(terminalViewAfterModification.getId()).getConfigUpdatedDate();

        assertNull(terminalViewAfterModification.getConfigUpdatedDate());
        assertNotEquals(beforeConfigUpdatedDate, afterConfigUpdatedDate);
        assertEquals("12346", terminalViewAfterModification.getServicePin());
        assertEquals("Argentina", terminalViewAfterModification.getCountry().getName());
        assertNotNull(terminalViewAfterModification.getAcquirers().stream().findFirst().get().getAcquirer().getCashbackLimit());
        assertEquals(10 , terminalViewAfterModification.getAcquirers().stream().findFirst().get().getAcquirer().getKeySettings().getMacMasterKeyIndex());
        assertEquals(4, terminalViewAfterModification.getAcquirers().stream().findFirst().get().getAcquirer().getKeySettings().getMacWorkingKeyIndex());
        assertEquals("10.10.10.10" , terminalViewAfterModification.getAcquirers().stream().findFirst().get().getAcquirer().getPaymentHost().getHost());
        PaymentSystemView afterModificationPaymentSystemView = terminalViewAfterModification.getAcquirers().stream().findFirst().get().getAcquirer().getPaymentSystems().stream().findFirst().get();
        assertEquals("11" , afterModificationPaymentSystemView.getContactlessAvn());
        assertEquals(1L ,  afterModificationPaymentSystemView.getDefaultLimits().getContactLimit().getFloorLimit());
        assertEquals(1,  afterModificationPaymentSystemView.getDefaultLimits().getContactLimit().getPinLimit());
        assertEquals(1L,  afterModificationPaymentSystemView.getDefaultLimits().getContactlessLimit().getFloorLimit());
        assertEquals(1L,  afterModificationPaymentSystemView.getDefaultLimits().getContactlessLimit().getNoDeviceCvmLimit());
        assertEquals("111", afterModificationPaymentSystemView.getContactlessApps().stream().filter(it -> contactlessApp.getId().equals(it.getId())).findFirst().get().getAid());
        assertEquals("111", afterModificationPaymentSystemView.getEmvApps().stream().filter(it -> emvApp.getId().equals(it.getId())).findFirst().get().getAid());
    }

    private void createAndAssertBadRequest(TerminalCreateDto createDto) {
        assertThrowsResponseStatusException(() -> terminalTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(TerminalCreateDto createDto, TerminalDto dto) {
        assertEquals(createDto.getSerialNumber(), dto.getSerialNumber());
        VendorFilterDto vendorFilterDto = new VendorFilterDto();
        Set<String> vendorModelNames = vendorTestClient.search(vendorFilterDto).getContent().get(0).getModels().stream().map(VendorModelDto::getName).collect(toSet());
        assertEquals(vendorModelNames.stream().map(modelName -> dto.getModel()).findFirst().get(), dto.getModel());
        assertEquals(createDto.getDefaultTerminalType().toString(), dto.getDefaultTerminalType().getSystemCode());
        if (isNotEmpty(createDto.getTerminalAcquirerParams())) {
            assertEquals(createDto.getTerminalAcquirerParams().size(), dto.getTerminalAcquirerParams().size());
            createDto.getTerminalAcquirerParams()
                    .forEach(expected -> assertTrue(dto.getTerminalAcquirerParams().stream()
                            .anyMatch(actual -> expected.getIsoTerminalId().equals(actual.getIsoTerminalId())
                                    && expected.getAcquirerId().equals(actual.getAcquirerId())
                                    && expected.getIsoMerchantId().equals(actual.getIsoMerchantId())
                                    && isNotBlank(actual.getAcquirerName()))));
        } else {
            assertTrue(isEmpty(dto.getTerminalAcquirerParams()));
        }
        assertEquals(createDto.getServicePin(), dto.getServicePin());
        assertEquals(createDto.getCountry(), ofNullable(dto.getCountry()).map(CountryDto::getName).orElse(null));
        assertEquals(createDto.getMerchantId(), dto.getMerchant().getId());
    }

    private void assertInputDtoMatchesDto(TerminalAcquirerParamsInputDto inputDto, TerminalAcquirerParamsDto dto) {
        assertEquals(inputDto.getAcquirerId(), dto.getAcquirerId());
        assertEquals(inputDto.getIsoTerminalId(), dto.getIsoTerminalId());
        assertEquals(inputDto.getIsoMerchantId(), dto.getIsoMerchantId());
        assertEquals(inputDto.getPinMasterKeyKCV(), dto.getPinMasterKeyKCV());
        assertEquals(inputDto.isRkiEnabled(), dto.isRkiEnabled());
    }

    private <T> void patchAndAssertOk(TerminalDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<TerminalPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, terminalTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, terminalTestClient.getById(dto.getId()));
        TerminalPatchDto patchDto = TerminalPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(ofNullable(newValue));
        assertEquals(dto, terminalTestClient.patch(patchDto));
        assertEquals(dto, terminalTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(TerminalPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> terminalTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

    public static String generateAccessToken() {
        Instant now = Instant.now();
        Algorithm algorithm = Algorithm.HMAC256("testsecretkey123456");

        return JWT.create()
                .withSubject("test-user-id")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(3600)))
                .withClaim("username", "test-user")
                .withClaim("role", "ADMIN")
                .sign(algorithm);
    }

}
