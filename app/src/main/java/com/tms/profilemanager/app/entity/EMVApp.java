package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Audited
@Data
@Entity
@Table(name = "emv_app")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EMVApp extends ArchivingBaseEntity {

    @Column(nullable = false)
    String aid;

    @Column(nullable = false)
    String terminalCaps;

    @Column(nullable = false)
    String terminalAdditionalCaps;

    @Column(nullable = false)
    String tacDefault;

    @Column(nullable = false)
    String tacOnline;

    @Column(nullable = false)
    String tacDenial;

    @Column(nullable = false)
    String tdol;

    @Column(nullable = false)
    String ddol;

    @Column(nullable = false)
    Long paymentSystemId;

    @Enumerated(EnumType.STRING)
    TerminalType terminalType;
}

