package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.PaymentScreenLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface PaymentScreenLogoRepository extends JpaRepository<PaymentScreenLogo, Long>, QuerydslPredicateExecutor<PaymentScreenLogo> {

    Optional<PaymentScreenLogo> findByImageId(String imageId);
}
