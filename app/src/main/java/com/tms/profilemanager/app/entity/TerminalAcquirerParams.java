package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class TerminalAcquirerParams extends BaseEntity {

    @Column(nullable = false)
    String merchantName;

    @Column(nullable = false)
    String isoTerminalId;

    @Column(nullable = false)
    String isoMerchantId;

    @Column(name = "pin_master_key_KCV")
    String pinMasterKeyKCV;

    OffsetDateTime kcvUpdatedDate;

    boolean rkiEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TerminalType type;

    String mcc;

    @OneToMany(mappedBy = "terminalAcquirerParams", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    List<TerminalAcquirerOperation> availableOperations = new ArrayList<>();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    List<String> paramsPaymentTypes;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", nullable = false)
    Terminal terminal;

    Long acquirerId;

    Integer paramsOrder;

    public Optional<TerminalAcquirerOperation> getAvailableOperation(AcquirerOperationType operation) {
        return availableOperations.stream()
                .filter(it -> it.getOperation().equals(operation))
                .findFirst();
    }

    public List<AcquirerOperationType> getOrderedAvailableOperations() {
        return availableOperations.stream()
                .sorted(Comparator.comparing(TerminalAcquirerOperation::getOperationOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(TerminalAcquirerOperation::getOperation)
                .collect(Collectors.toList());
    }

    public List<String> getParamsPaymentTypes() {
        return paramsPaymentTypes != null ? paramsPaymentTypes : Collections.emptyList();
    }

}
