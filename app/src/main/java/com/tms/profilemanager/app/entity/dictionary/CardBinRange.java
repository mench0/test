package com.tms.profilemanager.app.entity.dictionary;

import com.tms.profilemanager.app.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
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
public class CardBinRange extends BaseEntity {

    @Column(nullable = false)
    String startBin;

    @Column(nullable = false)
    String endBin;

    @ManyToOne
    @JoinColumn(name = "card_type_id", nullable = false)
    CardType cardType;

    @Audited
    @Column(name = "card_type_id", insertable = false, updatable = false)
    Long cardTypeId;

}
