package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
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
@FieldNameConstants
public class GeneralLimit extends BaseEntity {

    @Column(nullable = false)
    String name;


    @ManyToOne
    @JoinColumn(name = "contact_limit_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    ContactLimit contactLimit;

    @Audited
    @Column(name = "contact_limit_id", insertable = false, updatable = false)
    Long contactLimitId;

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

    @ToString.Include
    @EqualsAndHashCode.Include
    public Long getContactLimitId() {
        return contactLimit != null ? contactLimit.getId() : null;
    }
}
