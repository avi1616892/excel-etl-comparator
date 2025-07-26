package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelComparator {

    public static void main(String[] args) throws IOException {
        String file1 = "files/file1.xlsx";  // יחסית לתיקיית הפרויקט
        String file2 = "files/file2.xlsx";

        compareExcelFiles(file1, file2);
    }

    public static void compareExcelFiles(String path1, String path2) throws IOException {
        try (FileInputStream fis1 = new FileInputStream(path1);
             FileInputStream fis2 = new FileInputStream(path2);
             Workbook wb1 = new XSSFWorkbook(fis1);
             Workbook wb2 = new XSSFWorkbook(fis2);
             java.io.FileWriter writer = new java.io.FileWriter("differences.txt")) {  // פותחים קובץ לכתיבה

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
                        String line = String.format(
                                "Different at row %d, column %d: '%s' vs '%s'%n",
                                i + 1, j + 1, val1, val2);
                        writer.write(line);  // כותב לקובץ
                    }
                }
            }
            System.out.println("בדיקה הסתיימה. פתח את differences.txt לראות את ההבדלים.");
        }
    }


    private static String getCellValue(Row row, int cellIndex) {
        if (row == null) return "";
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
