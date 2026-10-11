package com.tms.profilemanager.app.migration;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Migration creates terminal role
 * Its name can be changed by REST API later
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1_0_2_CreateTerminalDefaultRole implements Migration {

    RoleService roleService;
    SecurityManager securityManager;

    private static final String TERMINAL_ROLE_NAME = "Terminal";

    @Override
    @Transactional
    public void execute() {
        securityManager.runAsSystemUser(() -> roleService.createTerminalDefaultRole(TERMINAL_ROLE_NAME));
    }
}
