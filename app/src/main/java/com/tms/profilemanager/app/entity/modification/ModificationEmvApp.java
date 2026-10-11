package com.tms.profilemanager.app.entity.modification;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
public class ModificationEmvApp extends BaseEntity {

    String aid;

    String terminalCaps;

    String terminalAdditionalCaps;

    String tacDefault;

    String tacOnline;

    String tacDenial;

    String tdol;

    String ddol;

    Long emvAppId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column=@Column(name = "terminal_type")),
            @AttributeOverride(name="ignored",
                    column=@Column(name = "terminal_type_ignored"))
    })
    EnumFieldModification<TerminalType> terminalType;

    @ManyToOne
    @JoinColumn(name = "modification_payment_system_id", nullable = false)
    ModificationPaymentSystem modificationEmvAppsPaymentSystem;

}
