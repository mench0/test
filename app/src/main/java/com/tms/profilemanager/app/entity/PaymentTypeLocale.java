package com.tms.profilemanager.app.entity;

import com.neovisionaries.i18n.LanguageCode;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class PaymentTypeLocale extends BaseEntity {

    @Column(nullable = false)
    String paymentName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    LanguageCode locale;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    PaymentType paymentType;

    @Audited
    @Column(name = "payment_type_id", insertable = false, updatable = false)
    Long paymentTypeId;
}
