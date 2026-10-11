package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LicenseRepository extends JpaRepository<License, Long>, QuerydslPredicateExecutor<License> {

    List<License> findAllBySerialNumberIn(Set<String> serialNumbers);

    Optional<License> findBySerialNumber(String serialNUmber);
}
