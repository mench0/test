package com.tms.profilemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto {

    private String entityName;
    private int revision;
    private long revisionTimestamp;
    private String revisionType;
    private String username;
    private String userIp;
    private Long userId;
    private Map<String, Object> data;
}
