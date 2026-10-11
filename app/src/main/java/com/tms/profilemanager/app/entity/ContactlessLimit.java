package com.tms.profilemanager.app.entity;

import com.neovisionaries.i18n.CurrencyCode;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessLimit extends ArchivingBaseEntity {

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    CurrencyCode currency;

    @Column(nullable = false)
    Long floorLimit;

    @Column(nullable = false)
    Long noCvmLimit;

    @Column(nullable = false)
    Long onDeviceCvmLimit;

    @Column(nullable = false)
    Long noDeviceCvmLimit;

    @Audited
    @OneToMany(mappedBy = "contactlessLimit", orphanRemoval = true, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    List<DRL> drlList;

    @Column(nullable = false)
    Long  maxAmount;

    /**
     * by default hibernate compares collections by identity ({@link PersistentBag})
     */
    @EqualsAndHashCode.Include
    protected List<DRL> getDrlListCopy() {
        return new ArrayList<>(drlList);
    }
}
