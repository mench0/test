package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long>, QuerydslPredicateExecutor<Merchant> {

    Optional<Merchant> findByName(String name);
}
