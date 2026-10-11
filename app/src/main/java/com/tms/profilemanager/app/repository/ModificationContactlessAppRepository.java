package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ModificationContactlessAppRepository extends JpaRepository<ModificationContactlessApp, Long>, QuerydslPredicateExecutor<ModificationContactlessApp> {


}
