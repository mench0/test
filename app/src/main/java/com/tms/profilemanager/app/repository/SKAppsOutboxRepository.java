package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.skapps.SKAppsOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface SKAppsOutboxRepository extends JpaRepository<SKAppsOutbox, Long>, QuerydslPredicateExecutor<SKAppsOutbox>  {

    List<SKAppsOutbox> findAllByIsBadRequestIsFalseOrderByIdAsc();
}
