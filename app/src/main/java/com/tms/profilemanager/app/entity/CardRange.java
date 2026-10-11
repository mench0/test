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
public class CardRange extends ArchivingBaseEntity {

    @Column(nullable = false)
    String low;

    @Column(nullable = false)
    String high;

    @Column(nullable = false)
    String name;

    Long acquirerId;

    @Audited
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "card_range_limit_mapping",
            joinColumns = @JoinColumn(name = "card_range_id"),
            inverseJoinColumns = @JoinColumn(name = "limit_id")
    )
    @EqualsAndHashCode.Exclude
    @Builder.Default
    Set<GeneralLimit> limits = new HashSet<>();
}
