package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.service.HumoSettingService;
import com.tms.profilemanager.dto.AcquirerDto;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.HumoSettingDto;
import com.tms.profilemanager.dto.VendorModelDto;
import com.tms.profilemanager.dto.create.HumoSettingCreateDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import com.tms.profilemanager.dto.filter.HumoSettingFilterDto;
import com.tms.profilemanager.dto.patch.HumoSettingPatchDto;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HumoSettingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    HumoSettingService humoSettingService;

    Long preparedModelId;
    Long preparedPaymentSystemId;
    Long preparedAcquirerId;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedModelId = vendorGenerator.createAndSave(1).getModels().stream().findFirst().get().getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        preparedAcquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId)).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        HumoSettingDto dto = humoSettingGenerator.createAndSave(preparedModelId, preparedAcquirerId);
        assertEquals(dto, humoSettingTestClient.getById(dto.getId()));
    }

    @Test
    void cacheGetAllTest() {
        clearInvocations(humoSettingRepository);

        // get from database because cache is empty
        assertTrue(humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl().isEmpty());
        verify(humoSettingRepository, times(1)).findAll();

        // get from database because result.size() == 0 (empty list are not cached)
        assertTrue(humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl().isEmpty());
        verify(humoSettingRepository, times(2)).findAll();

        // create new humo setting
        humoSettingGenerator.createAndSave(preparedModelId, preparedAcquirerId);
        Long preparedModelId1 = vendorGenerator.createAndSave(2).getModels().stream().findFirst().get().getId();
        Long preparedPaymentSystemId1 = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        Long preparedAcquirerId1 = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId1)).getId();
        humoSettingGenerator.createAndSave(preparedModelId1, preparedAcquirerId1);
        List<HumoSetting> createdHumoSettings = humoSettingRepository.findAll();

        clearInvocations(humoSettingRepository);

        // get from database because cache is empty
        assertEquals(createdHumoSettings, humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl());
        verify(humoSettingRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(2, humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl().size());
        verify(humoSettingRepository, times(1)).findAll();

        // patch humoSetting
        humoSettingTestClient.patch(HumoSettingPatchDto.builder()
                .id(createdHumoSettings.get(0).getId())
                .servicePin(Optional.of("99999"))
                .build());
        List<HumoSetting> patchedAcquirer = humoSettingRepository.findAll();
        assertEquals("99999", patchedAcquirer.stream()
                .filter(it -> it.getId().equals(createdHumoSettings.get(0).getId()))
                .findFirst().get().getServicePin());

        clearInvocations(humoSettingRepository);

        // get from database because cache is empty
        assertEquals(patchedAcquirer, humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl());
        verify(humoSettingRepository, times(1)).findAll();

        // get from cache and get the same result
        assertEquals(patchedAcquirer,  humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl());
        verify(humoSettingRepository, times(1)).findAll();

        // delete acquirer
        humoSettingTestClient.deleteById(createdHumoSettings.get(0).getId());

        clearInvocations(humoSettingRepository);

        // get from database because cache is empty
        assertEquals(1, humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl().size());
        verify(humoSettingRepository, times(1)).findAll();
    }

    @Test
    void searchTest() {
        Long preparedModelId2 = vendorGenerator.createAndSave(2).getModels().stream().findFirst().get().getId();
        Long preparedModelId3 = vendorGenerator.createAndSave(3).getModels().stream().findFirst().get().getId();

        humoSettingGenerator.createAndSave(preparedModelId, preparedAcquirerId);
        humoSettingGenerator.createAndSave(preparedModelId2, preparedAcquirerId);
        humoSettingGenerator.createAndSave(preparedModelId3, preparedAcquirerId);

        HumoSettingFilterDto filterDto = new HumoSettingFilterDto();
        List<Long> ids = humoSettingTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, humoSettingTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        HumoSettingCreateDto createDto = humoSettingGenerator.buildCreateDto(preparedModelId, preparedAcquirerId);

        createAndAssertBadRequest(createDto.toBuilder().humoId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().humoId("").build());
        createAndAssertBadRequest(createDto.toBuilder().humoId(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().humoId(repeat("*", 6)).build());

        createAndAssertBadRequest(createDto.toBuilder().acquirerId(null).build());

        createAndAssertBadRequest(createDto.toBuilder().modelId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().terminalType(null).build());

        createAndAssertBadRequest(createDto.toBuilder().servicePin(null).build());
        createAndAssertBadRequest(createDto.toBuilder().servicePin("").build());
        createAndAssertBadRequest(createDto.toBuilder().servicePin(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().servicePin("asdad").build());

        HumoSettingDto dto = humoSettingTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        HumoSettingDto humoSettingDto = humoSettingTestClient.getById(dto.getId());
        assertCreateDtoMatchesDto(createDto, humoSettingDto);
        assertEquals(vendorModelRepository.findById(createDto.getModelId()).get().getName(), humoSettingDto.getModel());
        assertEquals(acquirerRepository.findById(createDto.getAcquirerId()).get().getName(), humoSettingDto.getAcquirer());
    }

    @Test
    void patchTest() {
        HumoSettingDto dto = humoSettingGenerator.createAndSave(preparedModelId, preparedAcquirerId);
        HumoSettingPatchDto patchDto = HumoSettingPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().humoId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().humoId(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().humoId(Optional.of(" ")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().humoId(Optional.of(repeat("A", 6))).build());
        patchAndAssertOk(dto, repeat("a", 5), dto::setHumoId, p -> p::setHumoId);

        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of(" ")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of(repeat("1", 11))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().servicePin(Optional.of(repeat("A", 10))).build());
        patchAndAssertOk(dto, repeat("2", 10), dto::setServicePin, p -> p::setServicePin);

        patchAndAssertBadRequest(patchDto.toBuilder().terminalType(Optional.empty()).build());
        patchAndAssertOk(dto, TerminalType.ATTENDENT_12, dto::setTerminalType, p -> p::setTerminalType);

        patchAndAssertBadRequest(patchDto.toBuilder().acquirerId(Optional.empty()).build());
        AcquirerDto acquirerDto = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId));
        HumoSettingDto patch = humoSettingTestClient.patch(patchDto.toBuilder().acquirerId(Optional.of(acquirerDto.getId())).build());
        assertEquals(acquirerDto.getName(), humoSettingTestClient.getById(patch.getId()).getAcquirer());

        VendorModelDto vendorModelDto = vendorGenerator.createAndSave(2).getModels().stream().findFirst().get();
        HumoSettingDto patch1 = humoSettingTestClient.patch(patchDto.toBuilder().modelId(Optional.of(vendorModelDto.getId())).build());
        assertEquals(vendorModelDto.getName(), humoSettingTestClient.getById(patch1.getId()).getModel());
    }

    @Test
    void deleteByIdTest() {
        HumoSettingDto dto = humoSettingGenerator.createAndSave(preparedModelId, preparedAcquirerId);
        assertNotNull(humoSettingTestClient.getById(dto.getId()).getId());
        humoSettingTestClient.deleteById(dto.getId());
        humoSettingTestClient.assertNotExists(dto.getId());
        Optional<VendorModel> model = vendorModelRepository.findById(preparedModelId);
        assertEquals(preparedModelId, model.get().getId());
        assertEquals(preparedAcquirerId, acquirerTestClient.getById(preparedAcquirerId).getId());
    }

    private void createAndAssertBadRequest(HumoSettingCreateDto createDto) {
        assertThrowsResponseStatusException(() -> humoSettingTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(HumoSettingCreateDto createDto, HumoSettingDto dto) {
        assertEquals(createDto.getHumoId(), dto.getHumoId());
        assertEquals(createDto.getServicePin(), dto.getServicePin());
        assertEquals(createDto.getTerminalType(), dto.getTerminalType());
    }

    private <T> void patchAndAssertOk(HumoSettingDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<HumoSettingPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, humoSettingTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, humoSettingTestClient.getById(dto.getId()));
        HumoSettingPatchDto patchDto = HumoSettingPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, humoSettingTestClient.patch(patchDto));
        assertEquals(dto, humoSettingTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(HumoSettingPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> humoSettingTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
