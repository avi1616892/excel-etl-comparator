package com.example;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class ExcelComparatorTest {

    @Test
    void compareExcelFilesTest() throws IOException {
        String file1 = "files/file1.xlsx";
        String file2 = "files/file2.xlsx";

        // מפעילים את המחלקה שבודקת הבדלים
        DifferenceChecker checker = new DifferenceChecker();
        int diffCount = checker.compareExcelFiles(file1, file2);

        // אם יש הבדלים – נכשיל את הטסט
        if (diffCount > 0) {
            fail("נמצאו " + diffCount + " הבדלים בין הקבצים. ראה differences.txt");
        }
    }
}
