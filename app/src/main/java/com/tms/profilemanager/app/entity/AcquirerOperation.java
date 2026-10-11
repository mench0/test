package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Audited
@Data
@Entity
@Table(name = "acquirer_available_operations_mapping")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcquirerOperation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquirer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Acquirer acquirer;

    @Enumerated(EnumType.STRING)
    AcquirerOperationType operation;

    Integer operationOrder;

    public Long getAcquirerId() {
        return acquirer != null ? acquirer.getId() : null;
    }

}
