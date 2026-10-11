package com.tms.profilemanager.app.entity.modification;

import com.tms.profilemanager.app.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationPaymentSystem extends BaseEntity {

    String avn;

    String contactlessAvn;

    Long paymentSystemId;

    Long groupId;

    @Audited
    @OneToMany(mappedBy = "modificationEmvAppsPaymentSystem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    Set<ModificationEmvApp> emvApps = new HashSet<>();

    @Audited
    @OneToMany(mappedBy = "modificationContactlessAppsPaymentSystem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    Set<ModificationContactlessApp> contactlessApps = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "modification_limits_mapping", joinColumns = @JoinColumn(name = "modification_payment_system_Id"))
    @Column(name = "limit_id", nullable = false)
    @Builder.Default
    Set<Long> limitIds = new HashSet<>();

}
