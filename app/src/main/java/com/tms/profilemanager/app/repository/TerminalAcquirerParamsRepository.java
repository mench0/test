package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Set;

public interface TerminalAcquirerParamsRepository extends JpaRepository<TerminalAcquirerParams, Long> {

    @Query(value = """
            select t.id from terminal_acquirer_params t where t.acquirer_id = :acquirerId
            """, nativeQuery = true)
    Set<Long> findParamsIdsByAcquirerId(@Param("acquirerId") Long acquirerId);

    @Query(value = """
            select t.id as id, t.serial_number as serialNumber, t.created_at as createdAt, t.last_updated_at as lastUpdatedAt,
            t.group_id as groupId, t.config_updated_date as configUpdatedDate,  t.software_version as version, t.software_name as software, 
            t.mode as mode, t.software_vendor as vendor, t.software_model as model, m.name as merchantName, tap.iso_terminal_id as isoTerminalId, tap.iso_merchant_id as isoMerchantId,
            tap.pin_master_key_kcv as pinMasterKeyKcv, tap.kcv_updated_date as kcvUpdatedDate, a.name as acquirerName
            from terminal t
                     left join merchant m on m.id = t.merchant_id
                     left join terminal_acquirer_params tap on t.id = tap.terminal_id
                     left join acquirer a on tap.acquirer_id = a.id
            where t.group_id in (:ids)
            """, nativeQuery = true)
    Set<TerminalAcquirerParamsSlim> findParamsByTerminalGroupIds(@Param("ids") Set<Long> ids);

    interface TerminalAcquirerParamsSlim {
        Long getId();
        String getSerialNumber();
        Timestamp getCreatedAt();
        Timestamp getLastUpdatedAt();
        Long getGroupId();
        String getConfigUpdatedDate();
        String getVersion();
        String getSoftware();
        String getMode();
        String getVendor();
        String getModel();
        String getMerchantName();
        String getIsoTerminalId();
        String getIsoMerchantId();
        String getPinMasterKeyKcv();
        String getKcvUpdatedDate();
        String getAcquirerName();
    }
}
