package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.config.security.CurrentUserInfo;
import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.User;
import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.UserDto;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import com.tms.profilemanager.dto.patch.UsualUserPatchDto;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class UserMapper implements EntityMapper<User, UserDto> {

    @Override
    public UserDto toDto(User entity) {
        if (entity instanceof SystemUser systemUser) {
            return toDto(systemUser);
        }
        return toDto((UsualUser) entity);
    }

    protected abstract UsualUserDto toDto(UsualUser usualUser);

    protected abstract SystemUserDto toDto(SystemUser systemUser);

    public CurrentUserInfo toCurrentUserInfo(User user) {
        CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        updateCurrentUserInfo(currentUserInfo, user);
        return currentUserInfo;
    }

    public void updateCurrentUserInfo(CurrentUserInfo currentUserInfo, User user) {
        currentUserInfo.setUserId(user.getId());
        currentUserInfo.setUsername(user.getUsername());
        currentUserInfo.setArchived(user.isArchived());
        currentUserInfo.setRoleId(user.getRole().getId());
        currentUserInfo.setPasswordHash(user.getPasswordHash());
        currentUserInfo.setPermissions(user.getAllPermissions());
        currentUserInfo.setFullName(user.getFullName());
        if (user instanceof UsualUser usualUser) {
            currentUserInfo.setEmail(usualUser.getEmail());
            if (usualUser.getUserSettings() != null) {
                currentUserInfo.setLocale(usualUser.getUserSettings().getLocale());
            }
        } else if (user instanceof SystemUser systemUser) {
            currentUserInfo.setTerminalSerialNumber(systemUser.getTerminalSerialNumber());
        }
    }

    public UsualUserPatchDto usualUserCreateToUsualUserPatch(UsualUserCreateDto usualUserCreateDto, User user) {
        return  UsualUserPatchDto.builder()
                .id(user.getId())
                .email(Optional.of(usualUserCreateDto.getEmail()))
                .username(Optional.of(usualUserCreateDto.getUsername()))
                .fullName(Optional.of(usualUserCreateDto.getFullName()))
                .roleId(Optional.of(usualUserCreateDto.getRoleId()))
                .userSettings(UsualUserPatchDto.UserSettingsPatchDto.builder()
                        .locale(Optional.of(usualUserCreateDto.getUserSettings().getLocale()))
                        .build())
                .build();
    }
}
