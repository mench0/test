package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.create.modification.ModificationAcquirerCreateDto;
import com.tms.profilemanager.dto.filter.ModificationAcquirerFilterDto;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import com.tms.profilemanager.dto.patch.modification.ModificationAcquirerPatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModificationAcquirerIntegrationTest extends AbstractIntegrationTest {

    Long preparedAcquirerId;
    Long preparedPaymentSystemId1;
    Long preparedPaymentSystemId2;
    Long preparedTerminalGroupId;
    Long preparedContactLimitId;
    Long preparedContactlessLimitId;
    Long preparedGeneralLimitId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedTerminalGroupId = terminalGroupGenerator.createAndSave(1).getId();
        preparedContactLimitId = contactLimitGenerator.createAndSave(1).getId();
        preparedContactlessLimitId = contactlessLimitGenerator.createAndSave(1, List.of()).getId();
        preparedGeneralLimitId = generalLimitGenerator.createAndSave(1, preparedContactLimitId, preparedContactlessLimitId).getId();
        preparedPaymentSystemId1 = paymentSystemGenerator.createAndSave(1, preparedGeneralLimitId).getId();
        preparedPaymentSystemId2 = paymentSystemGenerator.createAndSave(2, preparedGeneralLimitId).getId();
        preparedAcquirerId = acquirerGenerator.createAndSave(1, Set.of(preparedPaymentSystemId1, preparedPaymentSystemId2)).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        ModificationAcquirerDto dto = modificationAcquirerGenerator.createAndSave(preparedAcquirerId, preparedTerminalGroupId);
        assertEquals(dto, modificationAcquirerTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        IntStream.range(2, 5).forEach(it -> {
            TerminalGroupDto groupDto = terminalGroupGenerator.createAndSave(it);
            Long groupId = groupDto.getId();
            modificationAcquirerGenerator.createAndSave(preparedAcquirerId, groupId);

        });
        ModificationAcquirerFilterDto filterDto = new ModificationAcquirerFilterDto();
        List<Long> ids = modificationAcquirerTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, modificationAcquirerTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        ModificationAcquirerCreateDto createDto = modificationAcquirerGenerator.buildCreateDto(preparedAcquirerId, preparedTerminalGroupId);

        createAndAssertBadRequest(createDto.toBuilder().acquirerId(null).build());
        createAndAssertBadRequest(createDto.toBuilder().groupId(null).build());

        ModificationAcquirerDto dto = modificationAcquirerTestClient.create(createDto);
        createAndAssertBadRequest(createDto.toBuilder().acquirerId(preparedAcquirerId).groupId(preparedTerminalGroupId).build());

        Long preparedAcquirerId1 = acquirerGenerator.createAndSave(2, Set.of(preparedPaymentSystemId1, preparedPaymentSystemId2)).getId();
        assertNotNull(modificationAcquirerGenerator.createAndSave(preparedAcquirerId1, preparedTerminalGroupId));

        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, modificationAcquirerTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        ModificationAcquirerDto dto = modificationAcquirerGenerator.createAndSave(preparedAcquirerId, preparedTerminalGroupId);
        ModificationAcquirerPatchDto patchDto = ModificationAcquirerPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().acquirerId(null).build());
        patchAndAssertBadRequest(patchDto.toBuilder().groupId(null).build());

        ModificationAcquirerDto patch = modificationAcquirerTestClient.patch(patchDto.toBuilder()
                .groupId(preparedTerminalGroupId)
                .acquirerId(preparedAcquirerId)
                .cashbackLimit(Optional.empty())
                .paymentHost(ModificationAcquirerPatchDto.ModificationPaymentHostPatchDto.builder()
                        .port(Optional.of(1111))
                        .host(Optional.empty())
                        .build())
                .build());
        assertNull(patch.getCashbackLimit());
        assertNull(patch.getPaymentHost().getHost());
        assertEquals(patch, modificationAcquirerTestClient.getById(patch.getId()));
        assertEquals(1111, patch.getPaymentHost().getPort());
    }


    private void assertCreateDtoMatchesDto(ModificationAcquirerCreateDto createDto, ModificationAcquirerDto dto) {
        assertEquals(createDto.getAcquirerId(), dto.getAcquirerId());
        assertEquals(createDto.getGroupId(), dto.getGroupId());
        assertEquals(createDto.getCashbackLimit(), dto.getCashbackLimit());
        assertEquals(createDto.getPaymentHost().getHost(), dto.getPaymentHost().getHost());
        assertEquals(createDto.getPaymentHost().getUseSsl(), dto.getPaymentHost().getUseSsl());
        assertEquals(createDto.getPaymentHost().getPort(), dto.getPaymentHost().getPort());

        assertEquals(createDto.getKeySettings().getKeyScheme(), dto.getKeySettings().getKeyScheme());
        assertEquals(createDto.getKeySettings().getMacWorkingKeyIndex(), dto.getKeySettings().getMacWorkingKeyIndex());
        assertEquals(createDto.getKeySettings().getPinWorkKeyIndex(), dto.getKeySettings().getPinWorkKeyIndex());
        assertEquals(createDto.getKeySettings().getPinMasterKeyIndex(), dto.getKeySettings().getPinMasterKeyIndex());
        assertEquals(createDto.getKeySettings().getMacMasterKeyIndex(), dto.getKeySettings().getMacMasterKeyIndex());
    }

    private void createAndAssertBadRequest(ModificationAcquirerCreateDto createDto) {
        assertThrowsResponseStatusException(() -> modificationAcquirerTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void patchAndAssertBadRequest(ModificationAcquirerPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> modificationAcquirerTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }

}
