package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.exception.PermissionsNotAvailableException;
import com.tms.profilemanager.app.exception.RoleEditFobiddenException;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.RoleService;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.dto.create.RoleCreateDto;
import com.tms.profilemanager.dto.enums.Permission;
import com.tms.profilemanager.dto.patch.RolePatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class RoleProvider extends EntityProvider<Role, RoleCreateDto, RolePatchDto> {

    @Autowired @Lazy
    RoleService roleService;

    @BeforeMapping
    protected void beforePatchEntity(RolePatchDto source, @MappingTarget Role target) {
        Set<Permission> currentUserPermissions = AuthenticationHolder.getCurrentUserInfoOrElseThrow().getPermissions();
        boolean userHasAllRolePermissions = currentUserPermissions.containsAll(target.getPermissions());
        if (!userHasAllRolePermissions) {
            Set<Permission> notAvailablePermissions = target.getPermissions().stream()
                    .filter(Predicate.not(currentUserPermissions::contains))
                    .collect(toSet());
            throw new RoleEditFobiddenException(notAvailablePermissions);
        }
        if(source.getPermissions() != null) {
            assertPermissionsAvailableForCurrentUser(source.getPermissions().getValues());
            patchSet(source.getPermissions(), target.getPermissions());
        }
    }

    @AfterMapping
    protected void afterInitializeEntity(RoleCreateDto source, @MappingTarget Role target) {
        if (source.getPermissions() != null) {
            assertPermissionsAvailableForCurrentUser(source.getPermissions());
        }
    }

    @Override
    @Mapping(target = "permissions", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(RolePatchDto patchDto, @MappingTarget Role entity);

    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    private void assertPermissionsAvailableForCurrentUser(Set<Permission> permissions) {
        Set<Permission> currentUserPermissions = AuthenticationHolder.getCurrentUserInfoOrElseThrow().getPermissions();
        Set<Permission> notAvailablePermissions = permissions.stream()
                .filter(Predicate.not(currentUserPermissions::contains))
                .collect(toSet());
        if (isNotEmpty(notAvailablePermissions)) {
            throw new PermissionsNotAvailableException(notAvailablePermissions);
        }
    }
}
