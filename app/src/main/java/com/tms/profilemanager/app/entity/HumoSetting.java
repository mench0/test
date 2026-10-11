package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.TerminalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class HumoSetting extends BaseEntity {

    @Column(nullable = false)
    String humoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TerminalType terminalType;

    @Column(nullable = false)
    String servicePin;

    Long modelId;

    Long acquirerId;

}
