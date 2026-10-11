package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ModificationAcquirerRepository extends JpaRepository<ModificationAcquirer, Long>, QuerydslPredicateExecutor<ModificationAcquirer> {

    List<ModificationAcquirer> findAllByGroupIdInOrderByGroupId(List<Long> groupIds);
}
