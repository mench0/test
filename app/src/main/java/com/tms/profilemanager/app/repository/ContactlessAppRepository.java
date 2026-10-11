package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.ContactlessApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ContactlessAppRepository extends
        JpaRepository<ContactlessApp, Long>, QuerydslPredicateExecutor<ContactlessApp> {

    List<ContactlessApp> findByPaymentSystemId(Long paymentSystemId);
}
