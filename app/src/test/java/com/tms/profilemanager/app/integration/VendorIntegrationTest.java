package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.VendorDto;
import com.tms.profilemanager.dto.VendorModelDto;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.create.VendorCreateDto.VendorModelInputDto;
import com.tms.profilemanager.dto.filter.VendorFilterDto;
import com.tms.profilemanager.dto.patch.VendorPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VendorIntegrationTest extends AbstractIntegrationTest {

    private Long preparedMerchantId;
    private Long preparedPaymentSystemId;
    private Long preparedAcquirerId;
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
        preparedPaymentTypeValue = paymentTypeGenerator.createAndSave(1).getPaymentValue();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        VendorDto dto = vendorGenerator.createAndSave(1);
        assertEquals(dto, vendorTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.range(1, 4).forEach(it -> vendorGenerator.createAndSave(it));
        VendorFilterDto filterDto = new VendorFilterDto();
        List<Long> ids = vendorTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(4, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, vendorTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        VendorCreateDto createDto = vendorGenerator.buildCreateDto(1);

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("*", 256)).build());

        createAndAssertBadRequest(createDto.toBuilder().models(null).build());
        createAndAssertBadRequest(createDto.toBuilder().models(Set.of()).build());

        VendorDto dto = vendorTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, vendorTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        VendorDto dto = vendorGenerator.createAndSave(1);
        VendorPatchDto patchDto = VendorPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("*", 101))).build());

        VendorDto patch = vendorTestClient.patch(patchDto.toBuilder().name(Optional.of(repeat("a", 100)))
                .models(Set.of(VendorModelInputDto.builder().name("model1").build())).build());
        VendorModelDto vendorModelDto = vendorTestClient.getById(patch.getId()).getModels().stream()
                .filter(vendorModel -> vendorModel.getName().equals("model1")).findFirst().get();
        terminalGenerator.createAndSave(1, preparedMerchantId, Set.of(preparedAcquirerId), vendorModelDto.getId(), preparedPaymentTypeValue);

        assertEquals(repeat("a", 100), patch.getName());

        assertEquals(1, vendorTestClient.getById(dto.getId()).getModels().size());
        patchAndAssertBadRequest(patchDto.toBuilder()
                .name(Optional.of("123"))
                .models(Set.of(VendorModelInputDto.builder().name("model2").build(),
                        VendorModelInputDto.builder().name("model3").build()))
                .build());
        assertEquals(1, vendorTestClient.getById(dto.getId()).getModels().size());

        VendorModelDto vendorModelDto1 = vendorTestClient.getById(dto.getId()).getModels().stream()
                .filter(vendorModel -> vendorModel.getName().equals("model1")).findFirst().get();
        vendorTestClient.patch(patchDto.toBuilder().models(Set.of(
                VendorModelInputDto.builder().id(vendorModelDto1.getId()).name("model2").build(),
                VendorModelInputDto.builder().name("model3").build())).build());

        vendorTestClient.patch(patchDto.toBuilder().models(Set.of(
                VendorModelInputDto.builder().id(vendorModelDto.getId()).name("model3").build())).build());

        VendorDto vendorDtoAfterPatching = vendorTestClient.getById(dto.getId());

        assertEquals(1, vendorDtoAfterPatching.getModels().size());

        patchAndAssertBadRequest(patchDto.toBuilder().models(Set.of(
                VendorModelInputDto.builder().id(vendorModelDto1.getId()).name("model2").build(),
                VendorModelInputDto.builder().name("model2").build())).build());
    }


    private void assertCreateDtoMatchesDto(VendorCreateDto createDto, VendorDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getModels().size(), dto.getModels().size());
    }

    private void createAndAssertBadRequest(VendorCreateDto createDto) {
        assertThrowsResponseStatusException(() -> vendorTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(VendorPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> vendorTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

}
