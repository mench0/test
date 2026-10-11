package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.ReceiptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ReceiptTemplateRepository extends JpaRepository<ReceiptTemplate, Long>, QuerydslPredicateExecutor<ReceiptTemplate> {

    @Query(value = """
            select * from receipt_template
            where merchant_id = :merchantId
              and active = true
              and valid_from <= current_timestamp
              and valid_to   >= current_timestamp
            order by id desc
            limit 1
                """, nativeQuery = true)
    Optional<ReceiptTemplate> findByMerchantId(@Param("merchantId") Long merchantId);

    Optional<ReceiptTemplate> findByExternalId(Long id);

    @Query("""
                select count(r) from ReceiptTemplate r
                where r.merchantId = :merchantId
                    and r.active = true
                    and r.validFrom < :validTo
                    and r.validTo > :validFrom
                    and r.validTo > :now
                    and r.externalId = :externalId
            """)
    long countActiveReceiptTemplate(
            @Param("merchantId") Long merchantId,
            @Param("validFrom") OffsetDateTime validFrom,
            @Param("validTo") OffsetDateTime validTo,
            @Param("now") OffsetDateTime now,
            @Param("externalId") Long externalId
    );
}
