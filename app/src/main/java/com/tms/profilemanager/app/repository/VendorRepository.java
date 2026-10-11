package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long>, QuerydslPredicateExecutor<Vendor> {

    Optional<Vendor> findByName(String name);

}
