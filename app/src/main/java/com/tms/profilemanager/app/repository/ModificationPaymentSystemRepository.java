package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ModificationPaymentSystemRepository extends JpaRepository<ModificationPaymentSystem, Long>, QuerydslPredicateExecutor<ModificationPaymentSystem> {

    List<ModificationPaymentSystem> findAllByGroupIdInOrderByGroupId(List<Long> groupIds);
    Optional<ModificationPaymentSystem> findByPaymentSystemIdAndGroupId(Long paymentSystemId, Long groupId);

}
