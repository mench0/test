package com.tms.profilemanager.app.test.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcelUtils {

    public static void assertExcel(byte[] bytes, byte[] templateFile) throws IOException {
        Workbook generatedWorkbook = new XSSFWorkbook(new ByteArrayInputStream(bytes));
        Workbook templateWorkbook = new XSSFWorkbook(new ByteArrayInputStream(templateFile));

        for (int i = 0; i < templateWorkbook.getNumberOfSheets(); i++) {
            Sheet templateSheet = templateWorkbook.getSheetAt(i);
            Sheet generatedSheet = generatedWorkbook.getSheetAt(i);

            int row = 0;
            while (row <= templateSheet.getLastRowNum()) {
                Row currentRow = templateSheet.getRow(row);

                if (currentRow == null || isRowEmpty(currentRow)) {
                    row++;
                    continue;
                }

                String firstCellValue = getCellValue(currentRow.getCell(0));
                if (firstCellValue.equals("Terminal") || firstCellValue.equals("TerminalAcquirerParams") || firstCellValue.equals("TerminalAcquirerOperation")) {

                    row++;
                    Row headerRow = templateSheet.getRow(row);
                    Map<Integer, String> columnIndexToName = buildHeaderMap(headerRow);

                    while (row <= templateSheet.getLastRowNum()) {
                        Row templateRow = templateSheet.getRow(row);
                        Row generatedRow = generatedSheet.getRow(row);

                        if (templateRow == null || isRowEmpty(templateRow)) break;

                        int lastCellNum = Math.max(templateRow.getLastCellNum(), generatedRow.getLastCellNum());

                        for (int cell = 0; cell < lastCellNum; cell++) {
                            String columnName = columnIndexToName.get(cell);
                            if (columnName != null && columnName.startsWith("revision date/time")) {
                                continue;
                            }

                            String templateValue = getCellValue(templateRow.getCell(cell));
                            String generatedValue = getCellValue(generatedRow.getCell(cell));

                            assertEquals(templateValue, generatedValue, "Mismatch at sheet " + i + " row " + row + " cell " + cell);
                        }

                        row++;
                    }

                } else {
                    row++;
                }
            }
        }
    }

    private static Map<Integer, String> buildHeaderMap(Row headerRow) {
        Map<Integer, String> map = new HashMap<>();
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                map.put(cell.getColumnIndex(), cell.toString().trim());
            }
        }
        return map;
    }

    private static boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && !getCellValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue();
            }
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) {
                    return String.valueOf((long) val);
                } else {
                    return String.valueOf(val);
                }
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            default -> {
                return cell.toString();
            }
        }
    }

}
