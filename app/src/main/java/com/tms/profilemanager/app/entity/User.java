package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.Permission;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Set;

@Audited
@Data
@Entity
@Table(name = "\"user\"")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class User extends ArchivingBaseEntity {

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String fullName;

    @NotAudited
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @Audited
    @Column(name = "role_id", insertable = false, updatable = false)
    Long roleId;

    @Column(nullable = false)
    String passwordHash;

    @Column(nullable = false)
    OffsetDateTime passwordLastSetAt;

    @Override
    protected void prePersist() {
        super.prePersist();
        passwordLastSetAt = getCreatedAt();
    }

    @NotAudited
    public Set<Permission> getAllPermissions() {
        return role != null ? role.getPermissions() : Set.of();
    }
}
