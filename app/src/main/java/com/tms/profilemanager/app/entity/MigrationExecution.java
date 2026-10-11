package com.tms.profilemanager.app.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.OffsetDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MigrationExecution {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "migrationExecutionSeq")
    @SequenceGenerator(name = "migrationExecutionSeq", sequenceName = "migration_execution_seq", allocationSize = 1)
    @Column(nullable = false, unique = true)
    Long id;

    @Column(name = "name")
    String name;

    String version;

    @Column(name = "execution_time")
    OffsetDateTime executionTime;
}
