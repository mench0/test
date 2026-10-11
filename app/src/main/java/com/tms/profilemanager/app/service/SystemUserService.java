package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.User;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.repository.UserRepository;
import com.tms.profilemanager.app.service.provider.SystemUserProvider;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.SystemUserPatchDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.tms.profilemanager.dto.enums.Permission.Values.SYSTEM_USER_WRITE;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemUserService {

    UserService userService;
    UserRepository userRepository;
    SystemUserProvider systemUserProvider;

    @Transactional(readOnly = true)
    public SystemUser getByIdOrElseThrow(@NotNull Long id) {
        User user = userService.getByIdOrElseThrow(id);
        if (!(user instanceof SystemUser)) {
            throw new EntityNotFoundByFieldException(SystemUser.class, "id", id);
        }
        return (SystemUser) user;
    }

    @Transactional(readOnly = true)
    public Optional<SystemUser> getByTerminalSerialNumber(String serialNumber) {
        return userService.searchOne(UserFilterDto.builder().terminalSerialNumber(serialNumber).build()).map(it -> (SystemUser) it);
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + SYSTEM_USER_WRITE + "')")
    public SystemUser create(@Valid SystemUserCreateDto createDto) {
        return userRepository.save(systemUserProvider.create(createDto));
    }

    @Transactional
    public SystemUser patch(@Valid SystemUserPatchDto patchDto) {
        SystemUser entity = getByIdOrElseThrow(patchDto.getId());
        userService.assertWriteAccess(entity);
        systemUserProvider.patch(patchDto, entity);
        return userRepository.save(entity);
    }

    @Transactional
    public void changeTerminalSerialNumber(@NotBlank String oldSerialNumber, @NotBlank String newSerialNumber) {
        if (oldSerialNumber.equals(newSerialNumber)) {
            return;
        }
        getByTerminalSerialNumber(oldSerialNumber)
                .ifPresent(systemUser -> {
                    systemUser.setTerminalSerialNumber(newSerialNumber);
                    systemUser.setUsername(newSerialNumber);
                });
    }
}
