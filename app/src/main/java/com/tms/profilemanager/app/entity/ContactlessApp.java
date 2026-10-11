package com.tms.profilemanager.app.entity;

import com.tms.profilemanager.dto.enums.TerminalType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.HashSet;
import java.util.Set;

@Audited
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "json", typeClass = JsonType.class)
public class ContactlessApp extends ArchivingBaseEntity {

    @Column(nullable = false)
    String aid;

    @Column(nullable = false)
    String kernelId;

    @Column(nullable = false)
    String kernelConfiguration;

    @Column(nullable = false)
    String terminalCaps;

    @Column(name = "terminal_caps_cvm_not_required", nullable = false)
    String terminalCapsCVMNotRequired;

    @Column(nullable = false)
    String terminalAdditionalCaps;

    @Column(nullable = false)
    String tacDefault;

    @Column(nullable = false)
    String tacOnline;

    @Column(nullable = false)
    String tacDenial;

    @Column(nullable = false)
    Long paymentSystemId;

    @Type(type = "json")
    @Column(nullable = false)
    @Builder.Default
    Set<String> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    TerminalType terminalType;
}
