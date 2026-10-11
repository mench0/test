package com.tms.profilemanager.app.integration;

import com.tms.profilemanager.app.service.UsualUserService;
import com.tms.profilemanager.app.test.utils.request.RequestSender;
import com.tms.profilemanager.dto.BaseDto;
import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.UserDto;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.UsualUserPatchDto;
import com.tms.profilemanager.dto.request.ChangeUserPasswordRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.testcontainers.shaded.com.google.common.base.Strings.repeat;

class UsualUserIntegrationTest extends AbstractIntegrationTest {

    @Autowired UsualUserService usualUserService;

    Long preparedRoleId;

    @BeforeEach
    public void beforeEach() {
        databaseCleaner.clearDatabaseAndExecuteJavaMigrations();
        preparedRoleId = roleGenerator.createAndSave(1, Permission.getAllPermissions()).getId();
        requestSender.setCurrentUser(ADMIN);
        doNothing().when(emailSender).send(any(), any(), any(), any());
    }

    @Test
    void getByIdTest() {
        UserDto dto = usualUserGenerator.createAndSave(1, preparedRoleId);
        assertEquals(dto, usualUserTestClient.getById(dto.getId()));
    }

    @Test
    void searchTest() {
        usualUserGenerator.createAndSave(1, preparedRoleId);
        usualUserGenerator.createAndSave(2, preparedRoleId);
        usualUserGenerator.createAndSave(3, preparedRoleId);

        UserFilterDto filterDto = new UserFilterDto();
        List<Long> ids = usualUserTestClient.search(filterDto).stream().map(BaseDto::getId).collect(toList());
        assertEquals(5, ids.size());
        filterDto.setIds(new HashSet<>(ids.subList(0, 2)));
        assertEquals(2, usualUserTestClient.search(filterDto).getTotalElements());
    }

    @Test
    void createTest() {
        UsualUserDto existingUserDto = usualUserGenerator.createAndSave(1, preparedRoleId);
        UsualUserCreateDto createDto = usualUserGenerator.buildCreateDto(2, preparedRoleId);

        createAndAssertBadRequest(createDto.toBuilder().username(null).build());
        createAndAssertBadRequest(createDto.toBuilder().username("").build());
        createAndAssertBadRequest(createDto.toBuilder().username(repeat("a", 51)).build());
        createAndAssertBadRequest(createDto.toBuilder().username("123*").build());
        createAndAssertBadRequest(createDto.toBuilder().username(existingUserDto.getUsername()).build());

        createAndAssertBadRequest(createDto.toBuilder().fullName(null).build());
        createAndAssertBadRequest(createDto.toBuilder().fullName("").build());
        createAndAssertBadRequest(createDto.toBuilder().fullName(repeat("*", 256)).build());

        createAndAssertBadRequest(createDto.toBuilder().email(null).build());
        createAndAssertBadRequest(createDto.toBuilder().email("").build());
        createAndAssertBadRequest(createDto.toBuilder().email("12345").build());
        createAndAssertBadRequest(createDto.toBuilder().email(existingUserDto.getEmail()).build());

        createAndAssertBadRequest(createDto.toBuilder().roleId(-1L).build());

        UsualUserDto dto = usualUserTestClient.create(createDto);
        assertCreateDtoMatchesDto(createDto, dto);
        assertCreateDtoMatchesDto(createDto, usualUserTestClient.getById(dto.getId()));

        UsualUserCreateDto newUsualUser = usualUserGenerator.buildCreateDto(3, preparedRoleId);
        newUsualUser.setEmail(existingUserDto.getEmail());

        usualUserTestClient.archiveById(existingUserDto.getId());
        UsualUserDto existingUserDtoBeforeArchived = usualUserTestClient.getById(existingUserDto.getId());
        assertTrue(existingUserDtoBeforeArchived.getArchived());
        usualUserTestClient.create(newUsualUser);
        usualUserTestClient.getById(existingUserDto.getId());
        UsualUserDto existingUserDtoAfterArchived = usualUserTestClient.getById(existingUserDto.getId());
        assertFalse(existingUserDtoAfterArchived.getArchived());
    }

    @Test
    void patchTest() {
        UsualUserDto existingUserDto = usualUserGenerator.createAndSave(1, preparedRoleId);
        UsualUserDto dto = usualUserGenerator.createAndSave(2, preparedRoleId);

        UsualUserPatchDto patchDto = UsualUserPatchDto.builder().id(dto.getId()).build();

        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of(repeat("a", 51))).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of("123z*")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().username(Optional.of(existingUserDto.getUsername())).build());
        assertEquals(dto, usualUserTestClient.patch(patchDto.toBuilder().username(Optional.of(dto.getUsername())).build()));
        patchAndAssertOk(dto, "new-test-username", dto::setUsername, p -> p::setUsername);

        patchAndAssertBadRequest(patchDto.toBuilder().fullName(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().fullName(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().fullName(Optional.of(repeat("*", 256))).build());
        patchAndAssertOk(dto, StringUtils.repeat("a", 14), dto::setFullName, p -> p::setFullName);

        patchAndAssertBadRequest(patchDto.toBuilder().email(Optional.empty()).build());
        patchAndAssertBadRequest(patchDto.toBuilder().email(Optional.of("")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().email(Optional.of("12345")).build());
        patchAndAssertBadRequest(patchDto.toBuilder().email(Optional.of(existingUserDto.getEmail())).build());
        assertEquals(dto, usualUserTestClient.patch(patchDto.toBuilder().email(Optional.of(dto.getEmail())).build()));
        patchAndAssertOk(dto, "newEmail1@mail.com", dto::setEmail, p -> p::setEmail);

        patchAndAssertBadRequest(patchDto.toBuilder().roleId(Optional.of(-1L)).build());
        RoleDto roleDto = roleGenerator.createAndSave(2, Permission.getAllPermissions());
        patchAndAssertOk(dto, roleDto, dto::setRole, p -> c -> p.setRoleId(c.map(BaseDto::getId)));
    }

    @Test
    void changePasswordByResetEmailLinkTest() {
        UsualUserDto userDto = usualUserGenerator.createAndSave(1, preparedRoleId);
        requestSender.setCurrentUser(userDto.getUsername(), RequestSender.DEFAULT_TEST_PASSWORD);
        assertEquals(userDto.getUsername(), usualUserTestClient.getCurrentUser().getUsername());

        requestSender.clearCurrentUser();
        ArgumentCaptor<String> emailTextCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(emailSender).send(any(), any(), emailTextCaptor.capture(), any());
        assertEquals(HttpStatus.OK, usualUserTestClient.sendPasswordResetLink(userDto.getEmail()));
        String emailText = emailTextCaptor.getValue();
        Matcher matcher = Pattern.compile("\\?token=([0-9]+)").matcher(emailText);
        assertTrue(matcher.find());
        String token = matcher.group(1);
        String newPassword = RequestSender.DEFAULT_TEST_PASSWORD + "new";

        assertEquals(HttpStatus.OK, usualUserTestClient.changePassword(ChangeUserPasswordRequestDto.builder()
                .secretToken(token)
                .newPassword(newPassword)
                .build()));

        try {
            requestSender.setCurrentUser(userDto.getUsername(), RequestSender.DEFAULT_TEST_PASSWORD);
            fail();
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
        requestSender.setCurrentUser(userDto.getUsername(), newPassword);
        assertEquals(userDto.getUsername(), usualUserTestClient.getCurrentUser().getUsername());
    }

    @Test
    void changePasswordTest() {
        UsualUserDto userDto = usualUserGenerator.createAndSave(1, preparedRoleId);
        requestSender.setCurrentUser(userDto.getUsername(), RequestSender.DEFAULT_TEST_PASSWORD);
        assertEquals(userDto.getUsername(), usualUserTestClient.getCurrentUser().getUsername());

        requestSender.clearCurrentUser();
        String newPassword = RequestSender.DEFAULT_TEST_PASSWORD + "new";
        assertEquals(HttpStatus.OK, usualUserTestClient.changePassword(ChangeUserPasswordRequestDto.builder()
                .userId(userDto.getId())
                .oldPassword(RequestSender.DEFAULT_TEST_PASSWORD)
                .newPassword(newPassword)
                .build()));

        try {
            requestSender.setCurrentUser(userDto.getUsername(), RequestSender.DEFAULT_TEST_PASSWORD);
            fail();
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
        requestSender.setCurrentUser(userDto.getUsername(), newPassword);
        assertEquals(userDto.getUsername(), usualUserTestClient.getCurrentUser().getUsername());
    }

    @Test
    void deleteByIdTest() {
        UserDto dto = usualUserGenerator.createAndSave(1, preparedRoleId);
        assertNotNull(usualUserTestClient.getById(dto.getId()).getId());
        usualUserTestClient.deleteById(dto.getId());
        usualUserTestClient.assertNotExists(dto.getId());
    }

    @Test
    void archiveAndRestoreByIdTest() {
        UserDto dto = usualUserGenerator.createAndSave(1, preparedRoleId);
        assertFalse(usualUserTestClient.getById(dto.getId()).getArchived());

        usualUserTestClient.archiveById(dto.getId());
        assertTrue(usualUserTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(usualUserService.getByIdOrElseThrow(dto.getId()).getLastArchivedAt()));

        usualUserTestClient.restoreById(dto.getId());
        assertFalse(usualUserTestClient.getById(dto.getId()).getArchived());
        securityManager.runAsSystemUser(() -> assertNotNull(usualUserService.getByIdOrElseThrow(dto.getId()).getLastRestoredAt()));
    }

    private void createAndAssertBadRequest(UsualUserCreateDto createDto) {
        assertThrowsResponseStatusException(() -> usualUserTestClient.create(createDto), HttpStatus.BAD_REQUEST);
    }

    private void assertCreateDtoMatchesDto(UsualUserCreateDto createDto, UsualUserDto dto) {
        assertEquals(createDto.getFullName(), dto.getFullName());
        assertEquals(createDto.getRoleId(), dto.getRole().getId());
        assertEquals(createDto.getEmail(), dto.getEmail());
    }

    private <T> void patchAndAssertOk(UserDto dto,
                                      T newValue,
                                      Consumer<T> dtoSetter,
                                      Function<UsualUserPatchDto, Consumer<Optional<T>>> patchDtoSetterSupplier) {
        assertEquals(dto, usualUserTestClient.getById(dto.getId()));
        dtoSetter.accept(newValue);
        assertNotEquals(dto, usualUserTestClient.getById(dto.getId()));
        UsualUserPatchDto patchDto = UsualUserPatchDto.builder().id(dto.getId()).build();
        patchDtoSetterSupplier.apply(patchDto).accept(Optional.of(newValue));
        assertEquals(dto, usualUserTestClient.patch(patchDto));
        assertEquals(dto, usualUserTestClient.getById(dto.getId()));
    }

    private void patchAndAssertBadRequest(UsualUserPatchDto patchDto) {
        assertThrowsResponseStatusException(() -> usualUserTestClient.patch(patchDto), HttpStatus.BAD_REQUEST);
    }
}
