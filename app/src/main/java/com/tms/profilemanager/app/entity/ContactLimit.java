package com.tms.profilemanager.app.entity;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
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

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactLimit extends ArchivingBaseEntity {

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    CurrencyCode currency;

    @Column(nullable = false)
    Long threshold;

    @Column(nullable = false)
    Integer targetPercent;

    @Column(nullable = false)
    Integer maxTargetPercent;

    @Column(nullable = false)
    Long floorLimit;

    @Column(nullable = false)
    Boolean pinBypassAllowed;

    @Column(nullable = false)
    Integer pinLimit;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = AcquirerOperationType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "request_last_4_digits_PAN_mapping")
    @Column(name = "operation_type", nullable = false)
    @Builder.Default
    Set<AcquirerOperationType> requestLast4DigitsPAN = new HashSet<>();
}
