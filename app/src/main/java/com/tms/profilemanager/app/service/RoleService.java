package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.entity.event.EntityEvent.OperationType;
import com.tms.profilemanager.app.mapper.filter.RoleSearchMapper;
import com.tms.profilemanager.app.repository.RoleRepository;
import com.tms.profilemanager.app.service.provider.RoleProvider;
import com.tms.profilemanager.dto.create.RoleCreateDto;
import com.tms.profilemanager.dto.filter.RoleFilterDto;
import com.tms.profilemanager.dto.patch.RolePatchDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tms.profilemanager.dto.enums.Permission.ROLE_WRITE;

@Service
public class RoleService extends AbstractCrudService<Role, RoleCreateDto, RolePatchDto, RoleFilterDto> {

    RoleRepository roleRepository;

    public RoleService(RoleProvider entityProvider,
                       RoleRepository repository,
                       RoleSearchMapper filterMapper,
                       ApplicationEventPublisher eventPublisher) {
        super(Role.class, entityProvider, repository, repository, filterMapper, eventPublisher);
        this.roleRepository = repository;
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(ROLE_WRITE);
    }

    @Transactional
    public Role createUsualRoleWithAllPermissions(String name) {
        assertWriteAccess();
        Role role = Role.builder()
                .name(name)
                .hasAllPermissions(true)
                .systemUserRole(false)
                .build();
        role = roleRepository.saveAndFlush(role);
        eventPublisher.publishEvent(new EntityEvent<>(Role.class, role.getId(), OperationType.INSERT));
        return role;
    }

    @Transactional
    public Role createTerminalDefaultRole(String name) {
        assertWriteAccess();
        Role role = Role.builder()
                .name(name)
                .terminalDefaultRole(true)
                .systemUserRole(true)
                .build();
        role = roleRepository.saveAndFlush(role);
        eventPublisher.publishEvent(new EntityEvent<>(Role.class, role.getId(), OperationType.INSERT));
        return role;
    }

    @Transactional(readOnly = true)
    public Role getTerminalDefaultRole() {
        return roleRepository.findTerminalDefaultRole()
                .orElseThrow(() -> new IllegalStateException("Terminal default role not found"));
    }
}
