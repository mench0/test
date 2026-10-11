package com.tms.profilemanager.app.integration;

import com.neovisionaries.i18n.LanguageCode;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto;
import com.tms.profilemanager.dto.filter.PaymentTypeFilterDto;
import com.tms.profilemanager.dto.patch.PaymentTypePatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static jodd.util.StringUtil.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentTypeIntegrationTest extends AbstractIntegrationTest {

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        PaymentTypeDto dto = paymentTypeGenerator.createAndSave(1);
        assertEquals(dto, paymentTypeTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.of(1, 2, 3).forEach((i) -> paymentTypeGenerator.createAndSave(i));
        PaymentTypeFilterDto filterDto = new PaymentTypeFilterDto();
        List<Long> ids = paymentTypeTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, paymentTypeTestClient.search(filterDto).getTotalElements());

        filterDto = new PaymentTypeFilterDto();
        List<PaymentTypeDto> payments = paymentTypeTestClient.search(filterDto).getContent();
        assertEquals(3, payments.size());
        assertEquals(ids.get(0), payments.get(0).getId());
    }

    @Test
    void createTest() {
        PaymentTypeCreateDto createDto = paymentTypeGenerator.buildCreateDto(2);
        createAndAssertBadRequest(createDto.toBuilder().paymentValue(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentValue("").build());
        createAndAssertBadRequest(createDto.toBuilder().paymentValue(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().paymentValue(repeat("1", 4)).build());

        PaymentTypeDto dto = paymentTypeTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, paymentTypeTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        PaymentTypeDto dto = paymentTypeGenerator.createAndSave(1);
        PaymentTypePatchDto patchDto = PaymentTypePatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().paymentValue(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentValue(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentValue(Optional.of(" ")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentValue(Optional.of(repeat("1", 4))).build());

        patchAndAssertBadRequest(patchDto.toBuilder().localizedNames(Set.of(PaymentTypeCreateDto.PaymentTypeLocaleInputDto.builder()
                .locale(null)
                .paymentName("12345")
                .build())).build());
        patchAndAssertBadRequest(patchDto.toBuilder().localizedNames(Set.of(PaymentTypeCreateDto.PaymentTypeLocaleInputDto.builder()
                .locale(LanguageCode.aa)
                .paymentName(null)
                .build())).build());
        patchAndAssertBadRequest(patchDto.toBuilder().localizedNames(Set.of(PaymentTypeCreateDto.PaymentTypeLocaleInputDto.builder()
                .locale(LanguageCode.ab)
                .paymentName("")
                .build())).build());
        patchAndAssertBadRequest(patchDto.toBuilder().localizedNames(Set.of(PaymentTypeCreateDto.PaymentTypeLocaleInputDto.builder()
                .locale(LanguageCode.en)
                .paymentName(" ")
                .build())).build());

        assertEquals(dto.getPaymentValue(), paymentTypeTestClient.patch(patchDto.toBuilder().paymentValue(Optional.of(dto.getPaymentValue())).build()).getPaymentValue());
        assertEquals(1, paymentTypeTestClient.patch(patchDto.toBuilder()
                .localizedNames(Set.of(PaymentTypeCreateDto.PaymentTypeLocaleInputDto.builder()
                        .locale(LanguageCode.ru)
                        .paymentName("12345")
                        .build()))
                .build()).getLocalizedNames().size());
    }

    @Test
    void deleteByIdTest() {
        PaymentTypeDto dto = paymentTypeGenerator.createAndSave(1);
        assertNotNull(paymentTypeTestClient.getById(dto.getId()).getId());
        paymentTypeTestClient.deleteById(dto.getId());
        paymentTypeTestClient.assertNotExists(dto.getId());
    }

    private void createAndAssertBadRequest(PaymentTypeCreateDto createDto) {
        assertThrowsResponseStatusException(() -> paymentTypeTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(PaymentTypeCreateDto createDto, PaymentTypeDto dto) {
        assertEquals(createDto.getPaymentValue(), dto.getPaymentValue());
    }

    private void patchAndAssertBadRequest(PaymentTypePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> paymentTypeTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
