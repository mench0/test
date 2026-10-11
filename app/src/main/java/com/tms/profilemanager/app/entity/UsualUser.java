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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Locale;

@Audited
@Data
@Entity
@Table(name = "usual_user")
@DiscriminatorValue("usual")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsualUser extends User {

    @Column(nullable = false)
    String email;

    @Embedded
    UserSettings userSettings;

    @Override
    protected void prePersist() {
        super.prePersist();
        if (username == null) {
            super.username = email;
        }
    }

    @Data
    @Builder
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserSettings {

        Locale locale;
    }
}
