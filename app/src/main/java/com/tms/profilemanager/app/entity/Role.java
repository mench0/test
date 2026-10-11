package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.Permission;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.stream;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends ArchivingBaseEntity {

    @Column(nullable = false, unique = true)
    String name;

    boolean hasAllPermissions;

    boolean systemUserRole;

    boolean terminalDefaultRole;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Permission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "permissions_mapping")
    @Column(name = "permission", nullable = false)
    @Builder.Default
    Set<Permission> permissions = new HashSet<>();

    public Set<Permission> getPermissions() {
        return hasAllPermissions ? Permission.getAllPermissions() : permissions;
    }

    public boolean hasAnyPermission(Permission... permissionsToCheck) {
        if (permissionsToCheck.length == 0) {
            return false;
        }
        return stream(permissionsToCheck).anyMatch(getPermissions()::contains);
    }

    public boolean hasAllPermissions(Permission... permissionsToCheck) {
        if (permissionsToCheck.length == 0) {
            return false;
        }
        return stream(permissionsToCheck).allMatch(getPermissions()::contains);
    }
}
