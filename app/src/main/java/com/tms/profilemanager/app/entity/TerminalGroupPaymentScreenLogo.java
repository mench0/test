package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Audited
@Data
@Entity
@Table(name = "terminal_group_payment_screen_logo")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalGroupPaymentScreenLogo extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "terminal_group_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    TerminalGroup terminalGroup;

    @ManyToOne
    @JoinColumn(name = "payment_screen_logo_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    PaymentScreenLogo paymentScreenLogo;

    Integer paymentScreenLogoOrder;

    public Long getPaymentScreenLogoId() {
        return paymentScreenLogo != null ? paymentScreenLogo.getId() : null;
    }

}
