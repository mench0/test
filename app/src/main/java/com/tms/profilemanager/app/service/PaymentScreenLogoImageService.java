package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.MinioProperties;
import com.tms.profilemanager.app.config.PaymentScreenLogoProperties;
import com.tms.profilemanager.app.exception.FileContentTypeException;
import com.tms.profilemanager.app.exception.FileNotFoundException;
import com.tms.profilemanager.app.exception.FileSizeExceededException;
import com.tms.profilemanager.dto.util.MinioFileData;
import com.tms.profilemanager.dto.util.PaymentScreenLogoImage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaymentScreenLogoImageService {

    private final PaymentScreenLogoProperties paymentScreenLogoProperties;
    private final MinioProperties minioProperties;
    private final MinioFileService minioFileService;

    public void deleteImageFromMinio(String fileId) {
        minioFileService.removeFileFromMinio(fileId, minioProperties.getPaymentScreenLogo().getBucket());
    }

    public PaymentScreenLogoImage downloadFile(String fileName) {
        MinioFileData response = minioFileService.download(fileName, minioProperties.getPaymentScreenLogo().getBucket());
        return PaymentScreenLogoImage.builder()
                .id(fileName)
                .contentType(response.getContentType())
                .content(response.getContent())
                .build();
    }

    @Transactional
    public String uploadToMinio(MultipartFile file) {
        validateFile(file);
        return minioFileService.uploadToMinio(file, minioProperties.getPaymentScreenLogo().getBucket());
    }

    public void validateFile(MultipartFile file) {
        if (!isAllowedContentType(file.getContentType())) {
            throw new FileContentTypeException(paymentScreenLogoProperties.getContentTypes());
        }

        long maxSizeBytes = paymentScreenLogoProperties.getMaxFileSize().toBytes();

        if (file.isEmpty()) {
            throw new FileNotFoundException();
        }

        if (file.getSize() > maxSizeBytes) {
            throw new FileSizeExceededException(paymentScreenLogoProperties.getMaxFileSize());
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return paymentScreenLogoProperties.getContentTypes().contains(contentType);
    }
}
