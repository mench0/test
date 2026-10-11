package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.RoleController;
import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.create.RoleCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleGenerator {

    RoleController roleController;

    @RunAsSystemUser
    public RoleDto createAndSave(int number, Set<Permission> permissions) {
        return roleController.create(buildCreateDto(number, permissions));
    }

    public RoleCreateDto buildCreateDto(int number, Set<Permission> permissions) {
        return RoleCreateDto.builder()
                .name("name" +  number)
                .permissions(permissions)
                .build();
    }
}
