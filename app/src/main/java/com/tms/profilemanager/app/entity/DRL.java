package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DRL extends ArchivingBaseEntity {

    @Column(nullable = false)
    Integer index;

    @Column(nullable = false)
    Long operationLimit;

    @Column(nullable = false)
    Long floorLimit;

    @Column(nullable = false)
    Long noCvmLimit;

    @ManyToOne
    @JoinColumn(name = "contactless_limit_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    ContactlessLimit contactlessLimit;

    @Audited
    @Column(name = "contactless_limit_id", insertable = false, updatable = false)
    Long contactlessLimitId;

    @ToString.Include
    @EqualsAndHashCode.Include
    public Long getContactlessLimitId() {
        return contactlessLimit != null ? contactlessLimit.getId() : null;
    }
}
