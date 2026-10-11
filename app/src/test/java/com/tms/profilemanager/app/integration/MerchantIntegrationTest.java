package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.MerchantService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.MerchantDto;
import com.tms.profilemanager.dto.create.MerchantCreateDto;
import com.tms.profilemanager.dto.filter.MerchantFilterDto;
import com.tms.profilemanager.dto.patch.MerchantPatchDto;
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

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MerchantIntegrationTest extends AbstractIntegrationTest {

    @Autowired MerchantService merchantService;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        MerchantDto dto = merchantGenerator.createAndSave(1);
        assertEquals(dto, merchantTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.range(1, 4).forEach(it -> merchantGenerator.createAndSave(it));

        MerchantFilterDto filterDto = new MerchantFilterDto();
        List<Long> ids = merchantTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, merchantTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        MerchantCreateDto createDto = merchantGenerator.buildCreateDto(1);

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("a", 256)).build());

        createAndAssertBadRequest(createDto.toBuilder().phone(null).build());
        createAndAssertBadRequest(createDto.toBuilder().phone("").build());
        createAndAssertBadRequest(createDto.toBuilder().phone(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().phone("1982371983718937").build());

        createAndAssertBadRequest(createDto.toBuilder().email(null).build());
        createAndAssertBadRequest(createDto.toBuilder().email("").build());
        createAndAssertBadRequest(createDto.toBuilder().email(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().email("123123").build());

        createAndAssertBadRequest(createDto.toBuilder().innOrPinfl(null).build());
        createAndAssertBadRequest(createDto.toBuilder().innOrPinfl("").build());
        createAndAssertBadRequest(createDto.toBuilder().innOrPinfl(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().innOrPinfl(repeat("1", 6)).build());
        createAndAssertBadRequest(createDto.toBuilder().innOrPinfl(repeat("1", 15)).build());

        assertDoesNotThrow(() -> merchantTestClient.create(createDto.toBuilder().address(null).build()));
        assertDoesNotThrow(() -> merchantTestClient.create(createDto.toBuilder()
                .name("1111")
                .address("")
                .phone("+79009001234")
                .email("45645@ya.ru")
                .innOrPinfl("444444444")
                .build()));
        createAndAssertBadRequest(createDto.toBuilder().address(repeat("a", 256)).build());

        createDto.setName("2222");
        createDto.setPhone("+79112223344");
        createDto.setEmail("aaa@gmail.com");
        createDto.setInnOrPinfl("4444444");
        MerchantDto dto = merchantTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, merchantTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        MerchantDto dto = merchantGenerator.createAndSave(1);
        MerchantPatchDto patchDto = MerchantPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("a", 256))).build());
        patchAndAssertOk(dto, repeat("a", 255), dto::setName, p -> p::setName);

        assertDoesNotThrow(() -> merchantTestClient.patch(patchDto.toBuilder().address(Optional.empty()).build()));
        assertDoesNotThrow(() -> merchantTestClient.patch(patchDto.toBuilder().address(Optional.of("")).build()));
        patchAndAssertBadRequest(patchDto.toBuilder().address(Optional.of(repeat("a", 256))).build());
        merchantTestClient.patch(patchDto.toBuilder().address(Optional.of("address1")).build());
        patchAndAssertOk(dto, repeat("a", 255), dto::setAddress, p -> p::setAddress);
    }

    @Test
    void deleteByIdTest() {
        MerchantDto dto = merchantGenerator.createAndSave(1);
        assertNotNull(merchantTestClient.getById(dto.getId()).getId());
        merchantTestClient.deleteById(dto.getId());
        merchantTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        MerchantDto dto = merchantGenerator.createAndSave(1);
        assertFalse(merchantTestClient.getById(dto.getId()).getArchived());

        merchantTestClient.archiveById(dto.getId());
        assertTrue(merchantTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(merchantService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        merchantTestClient.restoreById(dto.getId());
        assertFalse(merchantTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(merchantService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(MerchantCreateDto createDto) {
        assertThrowsResponseStatusException(() -> merchantTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(MerchantCreateDto createDto, MerchantDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getPhone(), dto.getPhone());
        assertEquals(createDto.getEmail(), dto.getEmail());
        assertEquals(createDto.getInnOrPinfl(), dto.getInnOrPinfl());
        assertEquals(createDto.getAddress(), dto.getAddress());
    }

    private <T> void patchAndAssertOk(MerchantDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<MerchantPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, merchantTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, merchantTestClient.getById(dto.getId()));
        MerchantPatchDto patchDto = MerchantPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.ofNullable(newValue));
        assertEquals(dto, merchantTestClient.patch(patchDto));
        assertEquals(dto, merchantTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(MerchantPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> merchantTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
