package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.HumoSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface HumoSettingRepository extends JpaRepository<HumoSetting, Long>, QuerydslPredicateExecutor<HumoSetting> {
}
