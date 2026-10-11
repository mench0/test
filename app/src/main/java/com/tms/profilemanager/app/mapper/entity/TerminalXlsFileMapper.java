package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.repository.TerminalAcquirerParamsRepository.TerminalAcquirerParamsSlim;
import com.tms.profilemanager.dto.TerminalExportDto;
import com.tms.profilemanager.dto.TerminalExportDto.TerminalAcquirerParamsExportDto;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class TerminalXlsFileMapper {

    public Map<Long, Set<TerminalExportDto>> toExportDto(Set<TerminalAcquirerParamsSlim> terminalSlims) {
        Map<String, Set<TerminalAcquirerParamsExportDto>> params = terminalSlims.stream()
                .collect(groupingBy(TerminalAcquirerParamsSlim::getSerialNumber, mapping(terminalSlim -> TerminalAcquirerParamsExportDto.builder()
                        .isoTerminalId(terminalSlim.getIsoTerminalId())
                        .isoMerchantId(terminalSlim.getIsoMerchantId())
                        .pinMasterKeyKCV(terminalSlim.getPinMasterKeyKcv())
                        .kcvUpdatedDate(terminalSlim.getKcvUpdatedDate())
                        .acquirerName(terminalSlim.getAcquirerName())
                        .build(), toSet())));
        return terminalSlims.stream()
                .filter(distinctByKey(TerminalAcquirerParamsSlim::getSerialNumber))
                .map(terminalSlim -> TerminalExportDto.builder()
                        .serialNumber(terminalSlim.getSerialNumber())
                        .createdAt(timestampToOffsetDateTime(terminalSlim.getCreatedAt()))
                        .lastUpdatedAt(timestampToOffsetDateTime(terminalSlim.getLastUpdatedAt()))
                        .groupId(terminalSlim.getGroupId())
                        .merchantName(terminalSlim.getMerchantName())
                        .terminalAcquirerParams(params.get(terminalSlim.getSerialNumber()))
                        .configUpdatedDate(terminalSlim.getConfigUpdatedDate())
                        .version(terminalSlim.getVersion())
                        .software(terminalSlim.getSoftware())
                        .mode(terminalSlim.getMode())
                        .vendor(terminalSlim.getVendor())
                        .model(terminalSlim.getModel())
                        .build())
                .collect(groupingBy(TerminalExportDto::getGroupId, TreeMap::new, toSet()));
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private OffsetDateTime timestampToOffsetDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime()
                .atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
    }
}
