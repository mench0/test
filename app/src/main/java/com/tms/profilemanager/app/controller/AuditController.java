package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.audit.AuditSection;
import com.tms.profilemanager.app.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "Аудит")
@RequiredArgsConstructor
public class AuditController {

    public static final String APPLICATION_XLSX_VALUE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final AuditService auditService;

    @GetMapping("/sections")
    @Operation(summary = "Получение списка секции")
    public List<AuditSection> getAllSections() {
        return List.of(AuditSection.values());
    }

    @GetMapping(value = "/download", produces = APPLICATION_XLSX_VALUE)
    public ResponseEntity<byte[]> downloadAuditBySections(
            @RequestParam List<AuditSection> sections,
            @RequestParam Long from,
            @RequestParam Long to
    ) throws IOException {
        Instant fromInstant = Instant.ofEpochMilli(from);
        Instant toInstant = Instant.ofEpochMilli(to);

        if (toInstant.isBefore(fromInstant)) {
            return ResponseEntity.badRequest()
                    .body("Parameter 'to' must be after 'from'.".getBytes());
        }

        long daysBetween = Duration.between(fromInstant, toInstant).toDays();
        if (daysBetween > 31) {
            return ResponseEntity.badRequest()
                    .body("Date range cannot exceed 31 days.".getBytes());
        }

        byte[] data = auditService.exportLogs(sections, from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit-logs.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_XLSX_VALUE)
                .body(data);
    }

}
