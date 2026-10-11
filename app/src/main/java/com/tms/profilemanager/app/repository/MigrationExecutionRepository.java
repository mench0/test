package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.MigrationExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MigrationExecutionRepository extends JpaRepository<MigrationExecution, Long> {

    Optional<MigrationExecution> findByName(String name);

    @Query("select m from MigrationExecution m where m.name <> :name and m.version >= :version")
    List<MigrationExecution> findMigrationsWithHigherVersionsThan(@Param("name") String name, @Param("version") String version);
}
