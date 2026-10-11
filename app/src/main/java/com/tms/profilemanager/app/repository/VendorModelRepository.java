package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.VendorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VendorModelRepository extends JpaRepository<VendorModel, Long>, QuerydslPredicateExecutor<VendorModel> {

    Optional<VendorModel> findByName(String name);

    @Query(value = "SELECT m.id as id, m.name as name from VendorModel m where m.id in :modelIds")
    List<ModelName> getModelsName(@Param("modelIds") Set<Long> modelIds);

    interface ModelName {
        Long getId();
        String getName();
    }
}
