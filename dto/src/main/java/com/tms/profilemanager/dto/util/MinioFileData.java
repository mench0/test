package com.tms.profilemanager.dto.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class MinioFileData {

    String id;

    String contentType;

    byte[] content;
}
