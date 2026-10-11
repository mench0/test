package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.ContactLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ContactLimitRepository extends
        JpaRepository<ContactLimit, Long>, QuerydslPredicateExecutor<ContactLimit> {
}
