package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ModificationEmvAppRepository extends JpaRepository<ModificationEmvApp, Long>, QuerydslPredicateExecutor<ModificationEmvApp> {

}
