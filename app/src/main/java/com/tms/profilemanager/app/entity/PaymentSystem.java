package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentSystem extends ArchivingBaseEntity {

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String avn;

    @Column(nullable = false)
    String contactlessAvn;

    @Audited
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "payment_system_limit_mapping",
            joinColumns = @JoinColumn(name = "payment_system_id"),
            inverseJoinColumns = @JoinColumn(name = "limit_id")
    )
    @EqualsAndHashCode.Exclude
    @Builder.Default
    Set<GeneralLimit> limits = new HashSet<>();

    @EqualsAndHashCode.Include
    protected Set<GeneralLimit> getLimitsCopy() {
        return new HashSet<>(limits);
    }
}
