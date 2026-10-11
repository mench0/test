package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseXlsFileServiceTest {

    @Mock
    LicenseService licenseService;
    @InjectMocks
    LicenseXlsFileService licenseXlsFileService;

    @Test
    void upload() {
        Map<String, License> snToLicense = parseFile();
        License license = License.builder().id(1L).serialNumber("serialNumber1").licenseCode("old_license").build();
        when(licenseService.findAllBySerialNumbers(snToLicense.keySet())).thenReturn(List.of(license));
        doNothing().when(licenseService).saveAllLicenses(Set.of(
                License.builder().licenseCode(snToLicense.get("234234234").getLicenseCode()).serialNumber("234234234").build(),
                License.builder().licenseCode(snToLicense.get("13123").getLicenseCode()).serialNumber("13123").build()));

        licenseXlsFileService.upload(getMultipartFile());

        verify(licenseService).patch(LicensePatchDto.builder()
                .id(license.getId())
                .licenseCode(Optional.of("3481325440"))
                .serialNumber(Optional.of("serialNumber1"))
                .build());
        verify(licenseService).saveAllLicenses(Set.of(
                License.builder().licenseCode(snToLicense.get("234234234").getLicenseCode()).serialNumber("234234234").build(),
                License.builder().licenseCode(snToLicense.get("13123").getLicenseCode()).serialNumber("13123").build()));
    }

    private Map<String, License> parseFile() {
        Map<String, License> snToLicense = new HashMap<>();
        try {
            File file = new File((Path.of("src/test/resources").toAbsolutePath() + "/1_2023-10-19 12_42_00.896.xls"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                XSSFCell cell1 = row.getCell(0);
                XSSFCell cell2 = row.getCell(1);
                cell1.setCellType(CellType.STRING);
                cell2.setCellType(CellType.STRING);
                String sn = cell1.getStringCellValue();
                String license = cell2.getStringCellValue();
                snToLicense.put(sn, License.builder()
                        .serialNumber(sn)
                        .licenseCode(license).build());            }
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        return snToLicense;
    }

    private MultipartFile getMultipartFile() {
        File file = new File((Path.of("src/test/resources").toAbsolutePath() + "/1_2023-10-19 12_42_00.896.xls"));
        FileInputStream input = null;
        MultipartFile multipartFile = null;
        try {
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file",
                    file.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, IOUtils.toByteArray(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return multipartFile;
    }
}