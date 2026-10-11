package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Acquirer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AcquirerRepository extends JpaRepository<Acquirer, Long>, QuerydslPredicateExecutor<Acquirer> {

    Optional<Acquirer> getByIdAndArchivedFalse(Long acquirerId);

    @Query(value = "SELECT a.id as id, a.name as name from Acquirer a where a.id in :acquirerIds")
    List<AcquirerName> getAcquirersName(@Param("acquirerIds") Set<Long> acquirerIds);

    interface AcquirerName {
        Long getId();
        String getName();
    }
}
