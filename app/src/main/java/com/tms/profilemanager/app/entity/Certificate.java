package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.OffsetDateTime;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Certificate extends ArchivingBaseEntity {

    @Column(nullable = false)
    String rid;

    @Column(nullable = false)
    Integer index;

    @Column(nullable = false)
    String exponent;

    @Column(nullable = false)
    String modulus;

    @Column(nullable = false)
    String hash;

    @Column(nullable = false)
    OffsetDateTime validUntil;

    @Column( nullable = false)
    Long paymentSystemId;
}
