package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.app.service.CertificateService;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.CertificateDto;
import com.tms.profilemanager.dto.create.CertificateCreateDto;
import com.tms.profilemanager.dto.filter.CertificateFilterDto;
import com.tms.profilemanager.dto.patch.CertificatePatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CertificateIntegrationTest extends AbstractIntegrationTest {

    @Autowired CertificateService certificateService;

    Long preparedPaymentSystemId;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        CertificateDto dto = certificateGenerator.createAndSave(1, preparedPaymentSystemId);
        assertEquals(dto, certificateTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByPaymentSystemIdTest() {
        clearInvocations(certificateRepository);

        // get from database because cache is empty
        assertTrue(certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache
        assertTrue(certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(1L).isEmpty());
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // create new certificate
        Long certificateId = certificateGenerator.createAndSave(1, preparedPaymentSystemId).getId();
        Certificate createdCertificate = certificateRepository.findById(certificateId).orElseThrow();

        clearInvocations(certificateRepository);

        // get from database because cache is empty
        assertEquals(createdCertificate, certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache and get the same result
        assertEquals(createdCertificate, certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // patch payment system
        certificateTestClient.patch(CertificatePatchDto.builder()
                .id(createdCertificate.getId())
                .rid(Optional.of("New rid"))
                .build());
        Certificate patchedCertificate = certificateRepository.findById(certificateId).orElseThrow();
        assertNotEquals(createdCertificate, patchedCertificate);

        clearInvocations(certificateRepository);

        // get from database because cache is empty
        assertEquals(patchedCertificate, certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // get from cache and get the same result
        assertEquals(patchedCertificate, certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).get(0));
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));

        // delete payment system
        certificateTestClient.deleteById(certificateId);

        clearInvocations(certificateRepository);

        // get from database because cache is empty
        assertTrue(certificateService.getByPaymentSystemIdUsingCacheWithoutAccessControl(preparedPaymentSystemId).isEmpty());
        verify(certificateRepository, times(1)).findByPaymentSystemId(any(Long.class));
    }

    @Test
    void searchTest() {
        Long paymentSystem2Id = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        certificateGenerator.createAndSave(1, preparedPaymentSystemId);
        certificateGenerator.createAndSave(2, paymentSystem2Id);
        certificateGenerator.createAndSave(3, paymentSystem2Id);

        CertificateFilterDto filterDto = new CertificateFilterDto();
        List<Long> ids = certificateTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, certificateTestClient.search(filterDto).getTotalElements());

        filterDto.setIds(null);
        filterDto.setPaymentSystemIds(Set.of(paymentSystem2Id));
        assertEquals(2, certificateTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        CertificateCreateDto createDto = certificateGenerator.buildCreateDto(1, preparedPaymentSystemId);

        createAndAssertBadRequest(createDto.toBuilder().rid(null).build());
        createAndAssertBadRequest(createDto.toBuilder().rid(repeat("*", 33)).build());

        createAndAssertBadRequest(createDto.toBuilder().index(null).build());

        createAndAssertBadRequest(createDto.toBuilder().exponent(null).build());
        createAndAssertBadRequest(createDto.toBuilder().exponent(repeat("*", 7)).build());

        createAndAssertBadRequest(createDto.toBuilder().modulus(null).build());

        createAndAssertBadRequest(createDto.toBuilder().hash(null).build());
        createAndAssertBadRequest(createDto.toBuilder().hash(repeat("*", 129)).build());

        createAndAssertBadRequest(createDto.toBuilder().validUntil(null).build());

        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().paymentSystemId(-1L).build());

        CertificateDto dto = certificateTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, certificateTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        CertificateDto dto = certificateGenerator.createAndSave(1, preparedPaymentSystemId);
        CertificatePatchDto patchDto = CertificatePatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().rid(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().rid(Optional.of(repeat("*", 33))).build());
        patchAndAssertOk(dto, "a", dto::setRid, p -> p::setRid);

        patchAndAssertBadRequest(patchDto.toBuilder().index(Optional.empty()).build());
        patchAndAssertOk(dto, dto.getIndex() + 1, dto::setIndex, p -> p::setIndex);

        patchAndAssertBadRequest(patchDto.toBuilder().exponent(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().exponent(Optional.of(repeat("*", 7))).build());
        patchAndAssertOk(dto, "a", dto::setExponent, p -> p::setExponent);

        patchAndAssertBadRequest(patchDto.toBuilder().modulus(Optional.empty()).build());
        patchAndAssertOk(dto, "a", dto::setModulus, p -> p::setModulus);

        patchAndAssertBadRequest(patchDto.toBuilder().hash(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().hash(Optional.of(repeat("*", 129))).build());
        patchAndAssertOk(dto, "a", dto::setHash, p -> p::setHash);

        patchAndAssertBadRequest(patchDto.toBuilder().validUntil(Optional.empty()).build());
        patchAndAssertOk(dto, ClockUtils.now(), dto::setValidUntil, p -> p::setValidUntil);

        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().paymentSystemId(Optional.of(-1L)).build());
        Long paymentSystemId = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        patchAndAssertOk(dto, paymentSystemId, dto::setPaymentSystemId, p -> p::setPaymentSystemId);
    }

    @Test
    void deleteByIdTest() {
        CertificateDto dto = certificateGenerator.createAndSave(1, preparedPaymentSystemId);
        assertNotNull(certificateTestClient.getById(dto.getId()).getId());
        certificateTestClient.deleteById(dto.getId());
        certificateTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        CertificateDto dto = certificateGenerator.createAndSave(1, preparedPaymentSystemId);
        assertFalse(certificateTestClient.getById(dto.getId()).getArchived());

        certificateTestClient.archiveById(dto.getId());
        assertTrue(certificateTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(certificateService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        certificateTestClient.restoreById(dto.getId());
        assertFalse(certificateTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(certificateService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(CertificateCreateDto createDto) {
        assertThrowsResponseStatusException(() -> certificateTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(CertificateCreateDto createDto, CertificateDto dto) {
        assertEquals(createDto.getRid(), dto.getRid());
        assertEquals(createDto.getIndex(), dto.getIndex());
        assertEquals(createDto.getExponent(), dto.getExponent());
        assertEquals(createDto.getModulus(), dto.getModulus());
        assertEquals(createDto.getHash(), dto.getHash());
        assertEquals(createDto.getValidUntil(), dto.getValidUntil());
        assertEquals(createDto.getPaymentSystemId(), dto.getPaymentSystemId());
    }

    private <T> void patchAndAssertOk(CertificateDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<CertificatePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, certificateTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, certificateTestClient.getById(dto.getId()));
        CertificatePatchDto patchDto = CertificatePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, certificateTestClient.patch(patchDto));
        assertEquals(dto, certificateTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(CertificatePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> certificateTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
