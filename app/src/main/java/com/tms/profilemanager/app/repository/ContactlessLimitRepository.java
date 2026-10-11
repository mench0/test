package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.ContactlessLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ContactlessLimitRepository extends
        JpaRepository<ContactlessLimit, Long>, QuerydslPredicateExecutor<ContactlessLimit> {
}
