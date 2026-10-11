package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.SystemUserService;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.UserDto;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.SystemUserPatchDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.com.google.common.base.Strings.repeat;

class SystemUserIntegrationTest extends AbstractIntegrationTest {

    @Autowired SystemUserService systemUserService;

    Long preparedRoleId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedRoleId = roleGenerator.createAndSave(1, Permission.getAllPermissions()).getId();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        UserDto dto = systemUserGenerator.createAndSave(1, preparedRoleId);
        assertEquals(dto, systemUserTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        systemUserGenerator.createAndSave(1, preparedRoleId);
        systemUserGenerator.createAndSave(2, preparedRoleId);
        systemUserGenerator.createAndSave(3, preparedRoleId);

        UserFilterDto filterDto = new UserFilterDto();
        List<Long> ids = systemUserTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(5, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, systemUserTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        SystemUserDto existingSystemUserDto = systemUserGenerator.createAndSave(1, preparedRoleId);
        SystemUserCreateDto createDto = systemUserGenerator.buildCreateDto(2, preparedRoleId);

        createAndAssertBadRequest(createDto.toBuilder().username(null).build());
        createAndAssertBadRequest(createDto.toBuilder().username("").build());
        createAndAssertBadRequest(createDto.toBuilder().username("abc123+").build());
        createAndAssertBadRequest(createDto.toBuilder().username(repeat("a", 51)).build());
        createAndAssertBadRequest(createDto.toBuilder().username(existingSystemUserDto.getUsername()).build());

        createAndAssertBadRequest(createDto.toBuilder().fullName(null).build());
        createAndAssertBadRequest(createDto.toBuilder().fullName("").build());
        createAndAssertBadRequest(createDto.toBuilder().fullName(repeat("*", 256)).build());

        createAndAssertBadRequest(createDto.toBuilder().roleId(-1L).build());

        SystemUserDto dto = systemUserTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, systemUserTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        SystemUserDto existingSystemUserDto = systemUserGenerator.createAndSave(1, preparedRoleId);
        SystemUserDto dto = systemUserGenerator.createAndSave(2, preparedRoleId);

        SystemUserPatchDto patchDto = SystemUserPatchDto.builder().id(dto.getId()).build();
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of("abcd123+")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of(repeat("a", 51))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of(existingSystemUserDto.getUsername())).build());
        assertEquals(dto, systemUserTestClient.patch(patchDto.toBuilder().username(Optional.of(dto.getUsername())).build()));
        patchAndAssertOk(dto, StringUtils.repeat("a", 14), dto::setUsername, p -> p::setUsername);

        patchAndAssertBadRequest(patchDto.toBuilder().fullName(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().fullName(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().fullName(Optional.of(repeat("*", 256))).build());
        patchAndAssertOk(dto, StringUtils.repeat("a", 4), dto::setFullName, p -> p::setFullName);

        patchAndAssertBadRequest(patchDto.toBuilder().roleId(Optional.of(-1L)).build());
        RoleDto roleDto = roleGenerator.createAndSave(2, Permission.getAllPermissions());
        patchAndAssertOk(dto, roleDto, dto::setRole, p -> c -> p.setRoleId(c.map(BaseDto::getId)));
    }

    @Test
    void deleteByIdTest() {
        UserDto dto = systemUserGenerator.createAndSave(1, preparedRoleId);
        assertNotNull(systemUserTestClient.getById(dto.getId()).getId());
        systemUserTestClient.deleteById(dto.getId());
        systemUserTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        UserDto dto = systemUserGenerator.createAndSave(1, preparedRoleId);
        assertFalse(systemUserTestClient.getById(dto.getId()).getArchived());

        systemUserTestClient.archiveById(dto.getId());
        assertTrue(systemUserTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(systemUserService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        systemUserTestClient.restoreById(dto.getId());
        assertFalse(systemUserTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(systemUserService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(SystemUserCreateDto createDto) {
        assertThrowsResponseStatusException(() -> systemUserTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(SystemUserCreateDto createDto, SystemUserDto dto) {
        assertEquals(createDto.getFullName(), dto.getFullName());
        assertEquals(createDto.getRoleId(), dto.getRole().getId());
    }

    private <T> void patchAndAssertOk(UserDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<SystemUserPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, systemUserTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, systemUserTestClient.getById(dto.getId()));
        SystemUserPatchDto patchDto = SystemUserPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, systemUserTestClient.patch(patchDto));
        assertEquals(dto, systemUserTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(SystemUserPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> systemUserTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
