package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.EMVApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EMVAppRepository extends
        JpaRepository<EMVApp, Long>, QuerydslPredicateExecutor<EMVApp> {

    List<EMVApp> findByPaymentSystemId(Long paymentSystemId);
}
