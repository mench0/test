package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long>, QuerydslPredicateExecutor<PaymentType>  {

    List<PaymentType> findAllByPaymentValueIn(List<String> paymentValues);
}
