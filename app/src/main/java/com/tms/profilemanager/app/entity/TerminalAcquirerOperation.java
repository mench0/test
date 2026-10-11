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
@Table(name = "terminal_acquirer_params_operations_mapping")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalAcquirerOperation extends BaseEntity{

    @Audited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_acquirer_params_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    TerminalAcquirerParams terminalAcquirerParams;

    @Enumerated(EnumType.STRING)
    AcquirerOperationType operation;

    Integer operationOrder;

    @ToString.Include
    @EqualsAndHashCode.Include
    public Long getTerminalAcquirerParams() {
        return terminalAcquirerParams != null ? terminalAcquirerParams.getId() : null;
    }
}
