package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.PaymentSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PaymentSystemRepository extends
        JpaRepository<PaymentSystem, Long>, QuerydslPredicateExecutor<PaymentSystem> {
}
