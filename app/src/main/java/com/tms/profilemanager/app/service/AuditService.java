package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.audit.AuditHandlerFactory;
import com.tms.profilemanager.app.entity.audit.AuditSection;
import com.tms.profilemanager.app.entity.audit.CustomRevisionEntity;
import com.tms.profilemanager.app.entity.audit.EntityMapper;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.AuditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tms.profilemanager.dto.enums.Permission.Values.AUDIT_READ;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final EntityManager entityManager;
    private final AuditHandlerFactory auditHandlerFactory;


    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('" + AUDIT_READ + "')")
    public byte[] exportLogs(List<AuditSection> sections, Long from, Long to) throws IOException {
        AuditReader reader = AuditReaderFactory.get(entityManager);
        Map<AuditSection, List<AuditDto>> result = new HashMap<>();

        for (AuditSection section : sections) {
            List<AuditDto> sectionEntries = new ArrayList<>();

            for (Class<?> entityClass : section.getEntityClasses()) {
                EntityMapper<Object> handler = auditHandlerFactory.getHandler(entityClass);

                List<Object[]> revisions = reader.createQuery()
                        .forRevisionsOfEntity(entityClass, false, true)
                        .add(AuditEntity.revisionProperty("timestamp").ge(from))
                        .add(AuditEntity.revisionProperty("timestamp").le(to))
                        .getResultList();

                for (Object[] revisionTuple : revisions) {
                    sectionEntries.add(toAuditDto(revisionTuple, handler));
                }
            }

            result.put(section, sectionEntries);
        }

        return exportAuditToExcel(result);
    }

    private AuditDto toAuditDto(Object[] revisionTuple, EntityMapper<Object> handler) {
        Object entity = revisionTuple[0];
        CustomRevisionEntity revEntity = (CustomRevisionEntity) revisionTuple[1];
        RevisionType revType = (RevisionType) revisionTuple[2];

        Map<String, Object> data = handler.toMap(entity);

        return AuditDto.builder()
                .entityName(handler.getEntityClass().getSimpleName())
                .revision(revEntity.getRev())
                .revisionTimestamp(revEntity.getTimestamp())
                .revisionType(revType.name())
                .username(revEntity.getUsername())
                .userIp(revEntity.getUserIp())
                .userId(revEntity.getUserId())
                .data(data)
                .build();
    }

    public byte[] exportAuditToExcel(Map<AuditSection, List<AuditDto>> auditData) throws IOException {
        try (Workbook workbook = new SXSSFWorkbook()) {

            for (Map.Entry<AuditSection, List<AuditDto>> entry : auditData.entrySet()) {
                AuditSection section = entry.getKey();
                List<AuditDto> audits = entry.getValue();

                if (audits.isEmpty()) continue;

                Sheet sheet = workbook.createSheet(section.name());
                int rowIdx = 0;

                for (Class<?> entityClass : section.getEntityClasses()) {
                    String entityName = entityClass.getSimpleName();

                    List<AuditDto> auditDtos = audits.stream()
                            .filter(dto -> dto.getEntityName().equals(entityName))
                            .toList();

                    if (auditDtos.isEmpty()) continue;

                    Row entityHeader = sheet.createRow(rowIdx++);
                    entityHeader.createCell(0).setCellValue(entityName);

                    Set<String> allKeys = auditDtos.stream()
                            .flatMap(dto -> dto.getData().keySet().stream())
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    Row headerRow = sheet.createRow(rowIdx++);
                    int colIdx = 0;
                    ZoneOffset zoneOffset = ClockUtils.getZone().getRules().getOffset(Instant.now());
                    String offset = zoneOffset.toString().equals("Z") ? "+00:00" : zoneOffset.toString();
                    headerRow.createCell(colIdx++).setCellValue("#");
                    headerRow.createCell(colIdx++).setCellValue("revision date/time(UTC" + offset + ")");
                    headerRow.createCell(colIdx++).setCellValue("revision type");
                    headerRow.createCell(colIdx++).setCellValue("username");
                    headerRow.createCell(colIdx++).setCellValue("userId");
                    headerRow.createCell(colIdx++).setCellValue("userIp");
                    for (String key : allKeys) {
                        headerRow.createCell(colIdx++).setCellValue(key);
                    }

                    int counter = 1;
                    for (AuditDto dto : auditDtos) {
                        Row row = sheet.createRow(rowIdx++);
                        colIdx = 0;
                        row.createCell(colIdx++).setCellValue(counter++);
                        row.createCell(colIdx++).setCellValue(Instant.ofEpochMilli(dto.getRevisionTimestamp())
                                .atZone(ClockUtils.getZone())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
                        row.createCell(colIdx++).setCellValue(dto.getRevisionType());
                        row.createCell(colIdx++).setCellValue(dto.getUsername());
                        row.createCell(colIdx++).setCellValue(String.valueOf(dto.getUserId()));
                        row.createCell(colIdx++).setCellValue(dto.getUserIp());
                        for (String key : allKeys) {
                            Object val = dto.getData().get(key);
                            row.createCell(colIdx++).setCellValue(val != null ? val.toString() : "");
                        }
                    }

                    rowIdx++;

                    int totalCols = 6 + allKeys.size();
                    for (int i = 0; i < totalCols; i++) {
                        sheet.setColumnWidth(i, 7000);
                    }
                }
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        }
    }

}
