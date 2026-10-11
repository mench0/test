package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface TerminalRepository extends JpaRepository<Terminal, Long>, QuerydslPredicateExecutor<Terminal> {

    Optional<Terminal> findBySerialNumberAndMerchantId(String serialNumber, Long merchantId);

    List<Terminal> findBySerialNumberIsInAndArchivedFalse(Set<String> serialNumbers);

    List<Terminal> findByMerchantIdAndArchivedFalseAndStatus(Long merchantId, Integer status);

    default Map<String, Terminal> findBySerialNumberIsInMap(Set<String> serialNumbers) {
        return findBySerialNumberIsInAndArchivedFalse(serialNumbers).stream().collect(Collectors.toMap(Terminal::getSerialNumber, terminal -> terminal));
    }

    Optional<Terminal> findFirstByVendorModelIdIn(Set<Long> modelId);

}
