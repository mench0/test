package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.dto.TerminalExportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TerminalXlsFileService {

    private final TerminalService terminalService;
    private final TerminalGroupService terminalGroupService;

    @Transactional(readOnly = true)
    public byte[] downloadTerminals(Long id) {
        List<TerminalGroup> groups = terminalGroupService.findByParentPath(id);

        Map<Long, TerminalGroup> groupIdToTerminalGroup = groups.stream().collect(Collectors.toMap(TerminalGroup::getId, Function.identity()));
        Set<Long> groupIds = groups.stream()
                .sorted(Comparator.comparing(TerminalGroup::getParentPath, Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(BaseEntity::getId).collect(Collectors.toSet());
        Map<Long, Set<TerminalExportDto>> terminalsByGroupIds = terminalService.findTerminalsByGroupIds(groupIds);
        XSSFWorkbook file = generateExcelFile(terminalsByGroupIds, groupIdToTerminalGroup);
        return convertExcelFileToByte(file);
    }

    private byte[] convertExcelFileToByte(XSSFWorkbook file) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            file.write(bos);
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            log.error("Ошибка при генерации файла excel", ex);
        }
        return bytes;
    }

    private XSSFWorkbook generateExcelFile(Map<Long, Set<TerminalExportDto>> groupIdToTerminals, Map<Long, TerminalGroup> groupIdToTerminalGroup) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Terminals");
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 14000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 7000);
        sheet.setColumnWidth(7, 7000);
        sheet.setColumnWidth(8, 7000);
        sheet.setColumnWidth(9, 7000);
        sheet.setColumnWidth(10, 7000);
        sheet.setColumnWidth(11, 5000);
        sheet.setColumnWidth(12, 5000);
        sheet.setColumnWidth(13, 5000);
        sheet.setColumnWidth(14, 5000);
        sheet.setColumnWidth(15, 5000);
        int i = 1;
        for (Map.Entry<Long, Set<TerminalExportDto>> terminals : groupIdToTerminals.entrySet()) {
            i = createTable(workbook, sheet, terminals, groupIdToTerminalGroup, i);
        }
        return workbook;
    }

    private int createTable(XSSFWorkbook workbook, XSSFSheet sheet, Map.Entry<Long, Set<TerminalExportDto>> terminals, Map<Long, TerminalGroup> groupIdToTerminalGroup, int i) {
        XSSFRow groupNameRow = sheet.createRow(i - 1);
        XSSFCell groupNameCell0 = groupNameRow.createCell(0);

        XSSFRow row = sheet.createRow(i);
        XSSFCell columnCell0 = row.createCell(0);
        XSSFCell columnCell1 = row.createCell(1);
        XSSFCell columnCell2 = row.createCell(2);
        XSSFCell columnCell3 = row.createCell(3);
        XSSFCell columnCell4 = row.createCell(4);
        XSSFCell columnCell5 = row.createCell(5);
        XSSFCell columnCell6 = row.createCell(6);
        XSSFCell columnCell7 = row.createCell(7);
        XSSFCell columnCell8 = row.createCell(8);
        XSSFCell columnCell9 = row.createCell(9);
        XSSFCell columnCell10 = row.createCell(10);
        XSSFCell columnCell11 = row.createCell(11);
        XSSFCell columnCell12 = row.createCell(12);
        XSSFCell columnCell13 = row.createCell(13);
        XSSFCell columnCell14 = row.createCell(14);
        XSSFCell columnCell15 = row.createCell(15);

        TerminalGroup terminalGroup = groupIdToTerminalGroup.get(terminals.getKey());
        String groupChain = imagingGroupNameChain(terminalGroup, groupIdToTerminalGroup);
        groupNameCell0.setCellValue(groupChain);
        sheet.addMergedRegion(new CellRangeAddress(i - 1, i - 1, 0, 8));
        XSSFCellStyle style = headStyle(workbook);
        columnCell0.setCellValue("#");
        columnCell0.setCellStyle(style);
        columnCell1.setCellValue("Serial number");
        columnCell1.setCellStyle(style);
        columnCell2.setCellValue("Merchant name");
        columnCell2.setCellStyle(style);
        columnCell3.setCellValue("PS");
        columnCell3.setCellStyle(style);
        columnCell4.setCellValue("MID");
        columnCell4.setCellStyle(style);
        columnCell5.setCellValue("TID");
        columnCell5.setCellStyle(style);
        columnCell6.setCellValue("PIN master key KCV");
        columnCell6.setCellStyle(style);
        columnCell7.setCellValue("Rkl_updated_date");
        columnCell7.setCellStyle(style);
        columnCell8.setCellValue("Create_date");
        columnCell8.setCellStyle(style);
        columnCell9.setCellValue("Modify_date");
        columnCell9.setCellStyle(style);
        columnCell10.setCellValue("Config_updated_date");
        columnCell10.setCellStyle(style);
        columnCell11.setCellValue("Version");
        columnCell11.setCellStyle(style);
        columnCell12.setCellValue("Software");
        columnCell12.setCellStyle(style);
        columnCell13.setCellValue("Mode");
        columnCell13.setCellStyle(style);
        columnCell14.setCellValue("Vendor");
        columnCell14.setCellStyle(style);
        columnCell15.setCellValue("Model");
        columnCell15.setCellStyle(style);

        int mainRow = i + 1;
        int terminalIndex = 1;
        for (TerminalExportDto terminal : terminals.getValue()) {
            XSSFRow data = sheet.createRow(mainRow);
            XSSFCell dataCell0 = data.createCell(0);
            XSSFCell dataCell1 = data.createCell(1);
            XSSFCell dataCell2 = data.createCell(2);
            XSSFCell dataCell3 = data.createCell(3);
            XSSFCell dataCell4 = data.createCell(4);
            XSSFCell dataCell5 = data.createCell(5);
            XSSFCell dataCell6 = data.createCell(6);
            XSSFCell dataCell7 = data.createCell(7);
            XSSFCell dataCell8 = data.createCell(8);
            XSSFCell dataCell9 = data.createCell(9);
            XSSFCell dataCell10 = data.createCell(10);
            XSSFCell dataCell11 = data.createCell(11);
            XSSFCell dataCell12 = data.createCell(12);
            XSSFCell dataCell13 = data.createCell(13);
            XSSFCell dataCell14 = data.createCell(14);
            XSSFCell dataCell15 = data.createCell(15);
            dataCell0.setCellValue(terminalIndex);
            dataCell1.setCellValue(terminal.getSerialNumber());
            dataCell2.setCellValue(terminal.getMerchantName());
            int paramsIndex = 1;
            for (TerminalExportDto.TerminalAcquirerParamsExportDto params : terminal.getTerminalAcquirerParams()) {
                if (paramsIndex == 1) {
                    dataCell3.setCellValue(params.getAcquirerName());
                    dataCell4.setCellValue(params.getIsoMerchantId());
                    dataCell5.setCellValue(params.getIsoTerminalId());
                    dataCell6.setCellValue(checkAndGetNullableField(params.getPinMasterKeyKCV()));
                    dataCell7.setCellValue(checkAndGetNullableField(params.getKcvUpdatedDate()));
                } else {
                    XSSFRow paramsRow = sheet.createRow(paramsIndex + mainRow - 1);
                    XSSFCell paramsCell3 = paramsRow.createCell(3);
                    XSSFCell paramsCell4 = paramsRow.createCell(4);
                    XSSFCell paramsCell5 = paramsRow.createCell(5);
                    XSSFCell paramsCell6 = paramsRow.createCell(6);
                    XSSFCell paramsCell7 = paramsRow.createCell(7);
                    paramsCell3.setCellValue(params.getAcquirerName());
                    paramsCell4.setCellValue(params.getIsoMerchantId());
                    paramsCell5.setCellValue(params.getIsoTerminalId());
                    paramsCell6.setCellValue(checkAndGetNullableField(params.getPinMasterKeyKCV()));
                    paramsCell7.setCellValue(checkAndGetNullableField(params.getKcvUpdatedDate()));
                }
                paramsIndex++;
            }

            dataCell8.setCellValue(terminal.getCreatedAt().toString());
            dataCell9.setCellValue(terminal.getLastUpdatedAt().toString());
            dataCell10.setCellValue(checkAndGetNullableField(terminal.getConfigUpdatedDate()));
            dataCell11.setCellValue(terminal.getVersion());
            dataCell12.setCellValue(terminal.getSoftware());
            dataCell13.setCellValue(checkAndGetNullableField(terminal.getMode()));
            dataCell14.setCellValue(terminal.getVendor());
            dataCell15.setCellValue(terminal.getModel());
            int paramsSize = terminal.getTerminalAcquirerParams().size();
            mainRow = paramsSize > 1 ? mainRow + paramsSize : mainRow + 2;
            terminalIndex++;
        }
        return mainRow + 1;
    }

    private XSSFCellStyle headStyle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        return style;
    }

    private String imagingGroupNameChain(TerminalGroup terminalGroup, Map<Long, TerminalGroup> groupIdToTerminalGroup) {
        String groupChain = null;
        if (terminalGroup.getParentPath() != null) {
            groupChain = Arrays.stream((terminalGroup.getParentPath() + "." + terminalGroup.getId()).split("\\."))
                    .map(Long::parseLong)
                    .filter(groupIdToTerminalGroup::containsKey)
                    .map(groupId -> groupIdToTerminalGroup.get(groupId).getName())
                    .collect(Collectors.joining(" - "));
        } else {
            groupChain = terminalGroup.getName();
        }
        return groupChain;
    }

    private String checkAndGetNullableField(String field) {
        if (StringUtils.isBlank(field)) {
            return "—";
        }
        return field;
    }
}
