package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CertificateRepository extends
        JpaRepository<Certificate, Long>, QuerydslPredicateExecutor<Certificate> {

    List<Certificate> findByPaymentSystemId(Long paymentSystemId);
}
