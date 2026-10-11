package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.LicenseService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.LicenseDto;
import com.tms.profilemanager.dto.create.LicenseCreateDto;
import com.tms.profilemanager.dto.filter.LicenseFilterDto;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LicenseIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    LicenseService licenseService;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        LicenseDto dto = licenseGenerator.createAndSave(1);
        assertEquals(dto, licenseTestClient.getById(dto.getId()));
    }

    @Test
    void getBySnTest() {
        LicenseDto dto = licenseGenerator.createAndSave(1);
        assertEquals(dto, licenseTestClient.getBySerialNumber(dto.getSerialNumber()));
    }

    @Test
    void searchTest() {
        IntStream.range(1, 4).forEach(it -> licenseGenerator.createAndSave(it));

        LicenseFilterDto filterDto = new LicenseFilterDto();
        List<Long> ids = licenseTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, licenseTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        LicenseCreateDto createDto = licenseGenerator.buildCreateDto(1);

        createAndAssertBadRequest(createDto.toBuilder().serialNumber(null).build());
        createAndAssertBadRequest(createDto.toBuilder().serialNumber("").build());
        createAndAssertBadRequest(createDto.toBuilder().serialNumber(repeat("a", 101)).build());

        createAndAssertBadRequest(createDto.toBuilder().licenseCode(null).build());
        createAndAssertBadRequest(createDto.toBuilder().licenseCode("").build());
        createAndAssertBadRequest(createDto.toBuilder().licenseCode(repeat("a", 101)).build());

        createDto.setLicenseCode("2222");
        LicenseDto dto = licenseTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, licenseTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        LicenseDto dto = licenseGenerator.createAndSave(1);
        LicensePatchDto patchDto = LicensePatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().licenseCode(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().licenseCode(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().licenseCode(Optional.of(repeat("a", 101))).build());
        patchAndAssertOk(dto, repeat("a", 100), dto::setLicenseCode, p -> p::setLicenseCode);

        patchAndAssertBadRequest(patchDto.toBuilder().serialNumber(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().serialNumber(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().serialNumber(Optional.of(repeat("a", 101))).build());
        patchAndAssertOk(dto, repeat("a", 100), dto::setSerialNumber, p -> p::setSerialNumber);
    }

    private void createAndAssertBadRequest(LicenseCreateDto createDto) {
        assertThrowsResponseStatusException(() -> licenseTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(LicenseCreateDto createDto, LicenseDto dto) {
        assertEquals(createDto.getSerialNumber(), dto.getSerialNumber());
        assertEquals(createDto.getLicenseCode(), dto.getLicenseCode());
    }
    private <T> void patchAndAssertOk(LicenseDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<LicensePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, licenseTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, licenseTestClient.getById(dto.getId()));
        LicensePatchDto patchDto = LicensePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(ofNullable(newValue));
        assertEquals(dto, licenseTestClient.patch(patchDto));
        assertEquals(dto, licenseTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(LicensePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> licenseTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
