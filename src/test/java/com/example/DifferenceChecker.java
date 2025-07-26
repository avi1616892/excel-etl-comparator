package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;

public class DifferenceChecker {

    public int compareExcelFiles(String path1, String path2) throws IOException {
        int diffCount = 0;
        try (FileInputStream fis1 = new FileInputStream(path1);
             FileInputStream fis2 = new FileInputStream(path2);
             Workbook wb1 = new XSSFWorkbook(fis1);
             Workbook wb2 = new XSSFWorkbook(fis2);
             FileWriter writer = new FileWriter("differences.txt")) {

            Sheet sheet1 = wb1.getSheetAt(0);
            Sheet sheet2 = wb2.getSheetAt(0);

            int maxRows = Math.max(sheet1.getLastRowNum(), sheet2.getLastRowNum());

            for (int i = 0; i <= maxRows; i++) {
                Row row1 = sheet1.getRow(i);
                Row row2 = sheet2.getRow(i);

                int maxCols = Math.max(
                        row1 != null ? row1.getLastCellNum() : 0,
                        row2 != null ? row2.getLastCellNum() : 0
                );

                for (int j = 0; j < maxCols; j++) {
                    String val1 = getCellValue(row1, j);
                    String val2 = getCellValue(row2, j);

                    if (!val1.equals(val2)) {
                        diffCount++;
                        String line = String.format(
                                "Different at row %d, column %d: '%s' vs '%s'%n",
                                i + 1, j + 1, val1, val2);
                        writer.write(line);
                    }
                }
            }
        }
        return diffCount;
    }

    private static String getCellValue(Row row, int cellIndex) {
        if (row == null) return "";
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
