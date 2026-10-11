package com.tms.profilemanager.app.migration;

import com.tms.profilemanager.app.config.security.SecurityProperties;
import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.entity.SystemUser;
import com.tms.profilemanager.app.entity.User;
import com.tms.profilemanager.app.repository.RoleRepository;
import com.tms.profilemanager.app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Migration creates default role for systems users and default system user
 * Their names can be changed by REST API later
 * System user's username and api key must be specified in config file
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1_0_0_CreateDefaultSystemUserAndRole implements Migration {

    /**
     * Using repositories instead of services to avoid access control
     */
    UserRepository userRepository;
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;
    SecurityProperties securityProperties;

    private static final String DEFAULT_SYSTEM_USER_ROLE_NAME = "Default system user role";
    private static final String DEFAULT_SYSTEM_USER_FULL_NAME = "Default system user";

    @Override
    @Transactional
    public void execute() {
        Role role = createSystemUserDefaultRole();
        createOrUpdateDefaultSystemUser(role);
    }

    private Role createSystemUserDefaultRole() {
        Role role = roleRepository.findByName(DEFAULT_SYSTEM_USER_ROLE_NAME);
        if (role != null) {
            throw new IllegalStateException("Role with name '" + DEFAULT_SYSTEM_USER_ROLE_NAME + "' already exists");
        }
        role = Role.builder()
                .name(DEFAULT_SYSTEM_USER_ROLE_NAME)
                .systemUserRole(true)
                .hasAllPermissions(true)
                .build();
        return roleRepository.save(role);
    }

    private void createOrUpdateDefaultSystemUser(Role role) {
        String username = securityProperties.getSystemUser().getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            throw new IllegalStateException("User with username '" + username + "' already exists");
        }
        SystemUser systemUser = SystemUser.builder()
                .username(username)
                .fullName(DEFAULT_SYSTEM_USER_FULL_NAME)
                .passwordHash(passwordEncoder.encode(securityProperties.getSystemUser().getPassword()))
                .role(role)
                .build();
        userRepository.save(systemUser);
    }
}
