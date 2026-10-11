package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class Terminal extends ArchivingBaseEntity {

    @Column(nullable = false, unique = true)
    String serialNumber;

    @Column(name = "model_id", nullable = false)
    Long vendorModelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TerminalType defaultTerminalType;

    @OneToMany(mappedBy = "terminal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    Set<TerminalAcquirerParams> terminalAcquirerParams = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Merchant merchant;

    @Audited
    @Column(name = "merchant_id", insertable = false, updatable = false)
    Long merchantId;

    String servicePin;

    @Column(nullable = false)
    String country;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    TerminalGroup group;

    @Audited
    @Column(name = "group_id", insertable = false, updatable = false)
    Long groupId;

    @Column(nullable = false)
    String address;

    @NotAudited
    OffsetDateTime configUpdatedDate;

    Integer status;

    String softwareVersion;

    String softwareName;

    String mode;

    String softwareVendor;

    String softwareModel;

    public Optional<TerminalAcquirerParams> getTerminalAcquirerParams(long acquirerId, String tid, String mid) {
        return terminalAcquirerParams.stream()
                .filter(it -> it.getAcquirerId().equals(acquirerId)
                        && it.getIsoTerminalId().equals(tid)
                        && it.getIsoMerchantId().equals(mid))
                .findFirst();
    }
}
