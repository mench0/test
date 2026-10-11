package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.License;
import com.tms.profilemanager.app.exception.FileNotFoundException;
import com.tms.profilemanager.app.exception.IncorrectFileException;
import com.tms.profilemanager.dto.patch.LicensePatchDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LicenseXlsFileService {

    LicenseService licenseService;

    @Transactional
    public void upload(MultipartFile file) {
        Set<License> licensesForSaving = new HashSet<>();
        Map<String, License> parsedLicenses = parseFile(file);
        List<License> foundedLicenses = licenseService.findAllBySerialNumbers(parsedLicenses.keySet());
        parsedLicenses.forEach((sn, license) -> {
            Optional<License> foundedLicense = foundedLicenses.stream()
                    .filter(lic -> lic.getSerialNumber().equals(sn))
                    .findFirst();
            if (foundedLicense.isPresent()) {
                License licenseToUpdating = foundedLicense.get();
                licenseService.patch(LicensePatchDto.builder()
                        .id(licenseToUpdating.getId())
                        .licenseCode(Optional.of(license.getLicenseCode()))
                        .serialNumber(Optional.of(sn))
                        .build());
            } else {
                licensesForSaving.add(license);
            }
        });

        licenseService.saveAllLicenses(licensesForSaving);
    }

    private Map<String, License> parseFile(MultipartFile file) {
        if (file == null) throw new FileNotFoundException();
        Map<String, License> snToLicense = new HashMap<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
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
                        .licenseCode(license).build());
            }
        } catch (IOException e) {
            throw new IncorrectFileException();
        }
        return snToLicense;
    }
}
