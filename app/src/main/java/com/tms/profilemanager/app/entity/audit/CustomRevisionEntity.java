package com.tms.profilemanager.app.entity.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity {

    @Id
    @GeneratedValue(generator = "custom_revision_seq")
    @SequenceGenerator(
            name = "custom_revision_seq",
            sequenceName = "custom_revision_entity_rev_seq",
            allocationSize = 1
    )
    @RevisionNumber
    private int rev;

    @RevisionTimestamp
    private long timestamp;

    private String username;

    private String userIp;

    private Long userId;
}
