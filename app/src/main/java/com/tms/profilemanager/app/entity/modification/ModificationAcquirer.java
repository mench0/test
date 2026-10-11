package com.tms.profilemanager.app.entity.modification;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.dto.enums.AcquirerKeyScheme;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModificationAcquirer extends BaseEntity {

    Long cashbackLimit;

    Long acquirerId;

    @Embedded
    ModificationKeySettings keySettings;

    @Embedded
    ModificationPaymentHost paymentHost;

    Long groupId;

    @Data
    @Embeddable
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModificationKeySettings {

        Integer pinMasterKeyIndex;

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
    public static class ModificationPaymentHost {

        String host;

        Integer port;

        Boolean useSsl;
    }

}
