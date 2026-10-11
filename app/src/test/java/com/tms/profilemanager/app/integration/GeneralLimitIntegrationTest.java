package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.create.limit.GeneralLimitCreateDto;
import com.tms.profilemanager.dto.filter.GeneralLimitFilterDto;
import com.tms.profilemanager.dto.patch.GeneralLimitPatchDto;
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
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class GeneralLimitIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    GeneralLimitService generalLimitService;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        GeneralLimitDto dto = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId);
        assertEquals(dto, generalLimitTestClient.getById(dto.getId()));
    }

    @Test
    void cacheByIdTest() {
        clearInvocations(generalLimitRepository);

        // get from database because cache is empty
        assertTrue(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(1L)).isEmpty());
        verify(generalLimitRepository, times(1)).findAllById(Set.of(1L));

        // get from database because result == null (null values are not cached)
        assertTrue(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(1L)).isEmpty());
        verify(generalLimitRepository, times(2)).findAllById(Set.of(1L));

        // create new general limit
        Long generalLimitId = createAndSaveGeneralLimit(1).getId();
        List<GeneralLimit> createdGeneralLimit = generalLimitRepository.findAllById(Set.of(generalLimitId));

        clearInvocations(generalLimitRepository);

        // get from database because cache is empty
        assertEquals(createdGeneralLimit, generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(generalLimitId)).stream().toList());
        verify(generalLimitRepository, times(1)).findAllById(Set.of(generalLimitId));

        // get from cache and get the same result
        assertEquals(createdGeneralLimit, generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(generalLimitId)).stream().toList());
        verify(generalLimitRepository, times(1)).findAllById(Set.of(generalLimitId));

        // patch general limit
        generalLimitTestClient.patch(GeneralLimitPatchDto.builder()
                .id(createdGeneralLimit.get(0).getId())
                .name(Optional.of("New name"))
                .contactLimitId(Optional.of(preparedContactLimitId))
                .contactlessLimitId(Optional.of(preparedContactlessLimitId))
                .build());
        List<GeneralLimit> patchedGeneralLimit = generalLimitRepository.findAllById(Set.of(generalLimitId));
        assertNotEquals(createdGeneralLimit, patchedGeneralLimit);

        clearInvocations(generalLimitRepository);

        // get from database because cache is empty
        assertEquals(patchedGeneralLimit, generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(generalLimitId)).stream().toList());
        verify(generalLimitRepository, times(1)).findAllById(Set.of(generalLimitId));

        // get from cache and get the same result
        assertEquals(patchedGeneralLimit, generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(generalLimitId)).stream().toList());
        verify(generalLimitRepository, times(1)).findAllById(Set.of(generalLimitId));

        // delete general limit
        generalLimitTestClient.deleteById(generalLimitId);

        clearInvocations(generalLimitRepository);

        // get from database because cache is empty
        assertTrue(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(Set.of(generalLimitId)).isEmpty());
        verify(generalLimitRepository, times(1)).findAllById(Set.of(generalLimitId));
    }

    @Test
    void searchTest() {
        IntStream.of(1, 2, 3).forEach(this::createAndSaveGeneralLimit);
        GeneralLimitFilterDto filterDto = new GeneralLimitFilterDto();
        List<Long> ids = generalLimitTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, generalLimitTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        Long contactLimitId = contactLimitGenerator.createAndSave(2).getId();
        Long contactlessLimitId = contactlessLimitGenerator.createAndSave(2, List.of()).getId();

        GeneralLimitCreateDto createDto = generalLimitGenerator.buildCreateDto(2, contactLimitId, contactlessLimitId);

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(" ").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("a", 51)).build());

        createAndAssertBadRequest(createDto.toBuilder().contactLimitId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().contactlessLimitId(null).build());


        createDto.setName("2222");
        GeneralLimitDto dto = generalLimitTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, generalLimitTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        Long contactLimitId = contactLimitGenerator.createAndSave(3).getId();
        Long contactlessLimitId = contactlessLimitGenerator.createAndSave(3, List.of()).getId();

        GeneralLimitDto dto = generalLimitGenerator.createAndSave(1, contactLimitId, contactlessLimitId);
        GeneralLimitPatchDto patchDto = GeneralLimitPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("a", 51))).build());
        patchAndAssertOk(dto, repeat("a", 50), dto::setName, p -> p::setName);

        patchAndAssertBadRequest(patchDto.toBuilder().contactLimitId(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().contactlessLimitId(Optional.empty()).build());
        GeneralLimitDto patch1 = generalLimitTestClient.patch(GeneralLimitPatchDto.builder()
                .id(patchDto.getId())
                .contactLimitId(Optional.of(contactLimitId))
                .contactlessLimitId(Optional.of(contactlessLimitId))
                .build());

        assertEquals(contactLimitId, patch1.getContactLimit().getId());
        assertEquals(contactlessLimitId, patch1.getContactlessLimit().getId());
    }

    private void createAndAssertBadRequest(GeneralLimitCreateDto createDto) {
        assertThrowsResponseStatusException(() -> generalLimitTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(GeneralLimitCreateDto createDto, GeneralLimitDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getContactLimitId(), dto.getContactLimit().getId());
        assertEquals(createDto.getContactlessLimitId(), dto.getContactlessLimit().getId());
    }

    private <T> void patchAndAssertOk(GeneralLimitDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<GeneralLimitPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, generalLimitTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, generalLimitTestClient.getById(dto.getId()));
        GeneralLimitPatchDto patchDto = GeneralLimitPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(ofNullable(newValue));
        assertEquals(dto, generalLimitTestClient.patch(patchDto));
        assertEquals(dto, generalLimitTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(GeneralLimitPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> generalLimitTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

    private GeneralLimitDto createAndSaveGeneralLimit(int number) {
        return generalLimitGenerator.createAndSave(number, preparedContactLimitId, preparedContactlessLimitId);
    }
}
