package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.RoleService;
import com.tms.profilemanager.app.test.utils.request.RequestSender;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.create.RoleCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.filter.RoleFilterDto;
import com.tms.profilemanager.dto.patch.BasePatchDto.SetPatch;
import com.tms.profilemanager.dto.patch.RolePatchDto;
import org.apache.commons.lang3.StringUtils;
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

import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.ADD;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REMOVE;
import static com.tms.profilemanager.dto.patch.BasePatchDto.CollectionPatchType.REPLACE;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleIntegrationTest extends AbstractIntegrationTest {

    @Autowired RoleService roleService;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        requestSender.setCurrentUser(ADMIN);
    }

    @Test
    void getByIdTest() {
        RoleDto dto = roleGenerator.createAndSave(1, Set.of(Permission.PAYMENT_SYSTEM_READ));
        assertEquals(dto, roleTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        RoleFilterDto filterDto = new RoleFilterDto();
        long rolesCount = roleTestClient.search(filterDto).getSize();

        IntStream.of(1, 2, 3).forEach(it -> roleGenerator.createAndSave(it, Set.of(Permission.PAYMENT_SYSTEM_READ)));

        List<Long> ids = roleTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(rolesCount + 3, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, roleTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        RoleDto existingRoleDto = roleGenerator.createAndSave(1, Set.of(Permission.PAYMENT_SYSTEM_READ));
        RoleCreateDto createDto = roleGenerator.buildCreateDto(2, Set.of(Permission.PAYMENT_SYSTEM_READ));

        createAndAssertBadRequest(createDto.toBuilder().name(null).build());
        createAndAssertBadRequest(createDto.toBuilder().name("").build());
        createAndAssertBadRequest(createDto.toBuilder().name(repeat("*", 256)).build());
        createAndAssertBadRequest(createDto.toBuilder().name(existingRoleDto.getName()).build());

        RoleDto dto = roleTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, roleTestClient.getById(dto.getId()));
    }

    @Test
    void patchTest() {
        RoleDto existingRoleDto = roleGenerator.createAndSave(1, Set.of(Permission.PAYMENT_SYSTEM_READ));
        RoleDto dto = roleGenerator.createAndSave(2, Set.of(Permission.PAYMENT_SYSTEM_READ));

        RolePatchDto patchDto = RolePatchDto.builder().id(dto.getId()).build();
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(repeat("*", 256))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().name(Optional.of(existingRoleDto.getName())).build());
        assertEquals(dto, roleTestClient.patch(patchDto.toBuilder().name(Optional.of(dto.getName())).build()));
        patchAndAssertOk(dto, StringUtils.repeat("a", 14), dto::setName, p -> p::setName);

        patchAndAssertOk(dto, Set.of(Permission.PAYMENT_SYSTEM_WRITE), dto::setPermissions,
                p -> value -> p.setPermissions(new SetPatch<>(value.orElse(null), REPLACE)));

        dto.setPermissions(Set.of());
        assertNotEquals(dto, roleTestClient.getById(dto.getId()));
        assertEquals(dto, roleTestClient.patch(patchDto.toBuilder()
                .permissions(new SetPatch<>(Set.of(Permission.PAYMENT_SYSTEM_WRITE), REMOVE))
                .build()));

        dto.setPermissions(Set.of(Permission.PAYMENT_SYSTEM_READ));
        assertNotEquals(dto, roleTestClient.getById(dto.getId()));
        assertEquals(dto, roleTestClient.patch(patchDto.toBuilder()
                .permissions(new SetPatch<>(Set.of(Permission.PAYMENT_SYSTEM_READ), ADD))
                .build()));
    }

    @Test
    void patchRoleByDifferentPermissionsTest(){
        RoleDto currentRole = roleGenerator.createAndSave(1, Set.of(Permission.ROLE_WRITE, Permission.ROLE_READ));
        UsualUserDto userDto = usualUserGenerator.createAndSave(1, currentRole.getId());
        requestSender.setCurrentUser(userDto.getUsername(), RequestSender.DEFAULT_TEST_PASSWORD);

        RoleDto updateRole = roleGenerator.createAndSave(2, Set.of(Permission.PAYMENT_SYSTEM_READ, Permission.ROLE_READ));
        RolePatchDto patchDto = RolePatchDto.builder().id(updateRole.getId())
                .permissions(new SetPatch<>(Set.of(Permission.ROLE_WRITE), ADD)).build();

        patchAndAssertForbidden(patchDto);

        RoleDto secondUpdateRole = roleGenerator.createAndSave(3, Set.of(Permission.ROLE_READ));
        patchDto = RolePatchDto.builder().id(secondUpdateRole.getId())
                .permissions(new SetPatch<>(Set.of(Permission.PAYMENT_SYSTEM_READ), REPLACE)).build();
        patchAndAssertForbidden(patchDto);

    }
    @Test
    void deleteByIdTest() {
        RoleDto dto = roleGenerator.createAndSave(1, Set.of(Permission.PAYMENT_SYSTEM_READ));
        assertNotNull(roleTestClient.getById(dto.getId()).getId());
        roleTestClient.deleteById(dto.getId());
        roleTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        RoleDto dto = roleGenerator.createAndSave(1, Set.of(Permission.PAYMENT_SYSTEM_READ));
        assertFalse(roleTestClient.getById(dto.getId()).getArchived());

        roleTestClient.archiveById(dto.getId());
        assertTrue(roleTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(roleService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        roleTestClient.restoreById(dto.getId());
        assertFalse(roleTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(roleService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(RoleCreateDto createDto) {
        assertThrowsResponseStatusException(() -> roleTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(RoleCreateDto createDto, RoleDto dto) {
        assertEquals(createDto.getName(), dto.getName());
        assertEquals(createDto.getPermissions(), dto.getPermissions());
    }

    private <T> void patchAndAssertOk(RoleDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<RolePatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, roleTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, roleTestClient.getById(dto.getId()));
        RolePatchDto patchDto = RolePatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, roleTestClient.patch(patchDto));
        assertEquals(dto, roleTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(RolePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> roleTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
    private void patchAndAssertForbidden(RolePatchDto patchDto) {
        assertThrowsResponseStatusException(() -> roleTestClient.patch(patchDto), HttpStatus.FORBIDDEN);
    }
}
