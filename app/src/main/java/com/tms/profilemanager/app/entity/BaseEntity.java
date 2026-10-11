package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.app.utils.ClockUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static org.hibernate.id.enhanced.SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY;

@Audited
@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "sequenceIdGenerator")
    @GenericGenerator(
            name = "sequenceIdGenerator",
            strategy = "sequence",
            parameters = @Parameter(name = CONFIG_PREFER_SEQUENCE_PER_ENTITY, value = "true")
    )
    @Column(nullable = false, unique = true)
    Long id;

    @NotAudited
    @Column(nullable = false)
    OffsetDateTime createdAt;

    @NotAudited
    Long createdBy;

    @NotAudited
    @Column(nullable = false)
    OffsetDateTime lastUpdatedAt;

    @NotAudited
    Long lastUpdatedBy;

    @PrePersist
    protected void prePersist() {
        createdAt = ClockUtils.now();
        lastUpdatedAt = createdAt;
        if (createdBy == null) {
            createdBy = AuthenticationHolder.getCurrentUserId().orElse(null);
        }
        lastUpdatedBy = createdBy;
    }

    @PreUpdate
    protected void preUpdate() {
        lastUpdatedAt = ClockUtils.now();
        if (lastUpdatedBy == null) {
            lastUpdatedBy = AuthenticationHolder.getCurrentUserId().orElse(null);
        }
    }
}
