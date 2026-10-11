package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Audited
@Data
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalGroup extends ArchivingBaseEntity {

    @Column(nullable = false, unique = true)
    String name;

    String parentPath;

    @OneToMany(mappedBy = "terminalGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    List<TerminalGroupPaymentScreenLogo> paymentScreenLogos = new ArrayList<>();

    public Long getParentId() {
        if (parentPath == null) {
            return null;
        }
        List<Long> parentIds = getAllParentIds();
        return parentIds.get(parentIds.size() - 1);
    }

    public List<Long> getAllParentIds() {
        return Arrays.stream(parentPath.split("\\."))
                .map(Long::parseLong)
                .toList();
    }

}
