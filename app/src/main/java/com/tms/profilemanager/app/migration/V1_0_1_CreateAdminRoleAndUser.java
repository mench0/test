package com.tms.profilemanager.app.migration;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.service.RoleService;
import com.tms.profilemanager.app.service.UsualUserService;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Migration creates admin role and admin user
 * Their names can be changed by REST API later
 * Admin email must be specified in config file
 * Also you can specify default admin password in config file, but it is optional
 * If default password is null then it will be generated automatically and sent to admin email
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1_0_1_CreateAdminRoleAndUser implements Migration {

    UsualUserService usualUserService;
    RoleService roleService;
    SecurityManager securityManager;

    @Value("${security.admin.default-email}")
    String adminEmail;
    @Value("${security.admin.default-password:#{null}}")
    String defaultPassword;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_FULL_NAME = "Administrator";
    private static final String ADMIN_ROLE_NAME = "Administrator";

    @Override
    @Transactional
    public void execute() {
        securityManager.runAsSystemUser(() -> {
            Role adminRole = roleService.createUsualRoleWithAllPermissions(ADMIN_ROLE_NAME);
            UsualUserCreateDto userCreateDto = UsualUserCreateDto.builder()
                    .username(ADMIN_USERNAME)
                    .email(adminEmail)
                    .fullName(ADMIN_FULL_NAME)
                    .roleId(adminRole.getId())
                    .build();
            if (defaultPassword != null) {
                userCreateDto.setPassword(defaultPassword);
            }
            usualUserService.create(userCreateDto);
        });
    }
}
