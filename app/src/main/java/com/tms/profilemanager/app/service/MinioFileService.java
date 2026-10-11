package com.tms.profilemanager.app.service;

import com.tms.profilemanager.dto.util.MinioFileData;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioFileService {

    MinioClient minioClient;

    public String uploadToMinio(MultipartFile file, String bucketName) {
        try {
            String objectName = String.valueOf(UUID.randomUUID());

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public MinioFileData download(String fileId, String bucket) {
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileId)
                        .build())) {
            return MinioFileData.builder()
                    .id(fileId)
                    .contentType(response.headers().get("Content-Type"))
                    .content(response.readAllBytes())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while loading file. fileId=" + fileId, e);        }
    }

    public void removeFileFromMinio(String fileId, String bucket) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(fileId).build());
            log.info("File successfully removed from MinIO. bucket={}, objectKey={}", bucket, fileId);

        } catch (Exception e) {
            log.error("Failed to remove file from MinIO. bucket={}, objectKey={}", bucket, fileId, e);
        }
    }


}
