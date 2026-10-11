package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.DRL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DRLRepository extends JpaRepository<DRL, Long>, QuerydslPredicateExecutor<DRL> {
}
