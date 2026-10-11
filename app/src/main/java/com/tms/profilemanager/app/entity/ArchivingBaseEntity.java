package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Audited
@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class ArchivingBaseEntity extends BaseEntity implements Serializable {

    @Column(nullable = false)
    boolean archived;

    @NotAudited
    OffsetDateTime lastArchivedAt;

    @NotAudited
    OffsetDateTime lastRestoredAt;
}
