package com.tms.profilemanager.app.entity;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import com.tms.profilemanager.dto.enums.IsoProtocol;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Audited
@Slf4j
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class Acquirer extends ArchivingBaseEntity {

    @Column(nullable = false, unique = true)
    String name;

    @Audited
    @OneToMany(mappedBy = "acquirer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    Set<AcquirerOperation> availableOperations = new HashSet<>();

    @Audited
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CurrencyCode.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "acquirer_available_currencies_mapping", joinColumns = @JoinColumn(name = "acquirer_id"))
    @Column(name = "currency", nullable = false)
    @Builder.Default
    Set<CurrencyCode> availableCurrencies = new HashSet<>();

    @Column(nullable = false)
    Boolean usingMacToSignRequests;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    IsoProtocol isoProtocol;

    @Audited
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "acquirer_available_payment_systems_mapping", joinColumns = @JoinColumn(name = "acquirer_id"))
    @Column(name = "payment_system_id")
    Set<Long> paymentSystemIds;

    @Embedded
    KeySettings keySettings;

    @Embedded
    PaymentHost paymentHost;

    @Embedded
    RklUrl rklUrl;

    Long cashbackLimit;

    @Data
    @Embeddable
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeySettings implements Serializable {

        @Column(nullable = false)
        Integer pinMasterKeyIndex;

        @Column(nullable = false)
        Integer pinWorkKeyIndex;

        Integer macMasterKeyIndex;

        Integer macWorkingKeyIndex;

        @Enumerated(EnumType.STRING)
        AcquirerKeyScheme keyScheme;
    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentHost implements Serializable {

        @Column(nullable = false)
        String host;

        @Column(nullable = false)
        Integer port;

        @Column(nullable = false)
        Boolean useSsl;
    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RklUrl implements Serializable {

        Boolean rklEnable;

        String rklHost;

        Integer rklPort;

        String rklPath;

        Boolean rklUseSsl;
    }

    public Optional<AcquirerOperation> getAvailableOperation(AcquirerOperationType operation) {
        return availableOperations.stream()
                .filter(it -> it.getOperation().equals(operation))
                .findFirst();
    }

    public List<AcquirerOperationType> getOrderedAvailableOperations() {
        return availableOperations.stream()
                .sorted(Comparator.comparingInt(AcquirerOperation::getOperationOrder))
                .map(AcquirerOperation::getOperation)
                .collect(Collectors.toList());
    }
}
