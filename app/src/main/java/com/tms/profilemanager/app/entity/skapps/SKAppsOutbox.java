package com.tms.profilemanager.app.entity.skapps;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.dto.enums.EntityType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Slf4j
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class SKAppsOutbox extends BaseEntity {

    @Enumerated(EnumType.STRING)
    EntityType entityType;

    Long entityId;

    boolean isBadRequest;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    String payload;

    @Column(nullable = false)
    int retryCount = 0;
}
