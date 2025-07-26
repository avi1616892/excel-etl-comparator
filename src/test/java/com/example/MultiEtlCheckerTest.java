package com.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.fail;

public class MultiEtlCheckerTest {

    @Test
    void testMultiCsvComparison() throws Exception {
        MultiEtlChecker checker = new MultiEtlChecker();
        try (Connection conn = checker.createConnection()) {

            // טבלאות שנרצה לבדוק
            String[] tables = {"customers", "accounts", "transactions"};
            int totalDiffs = 0;

            for (String table : tables) {
                checker.loadCsvIntoTable(conn, "files/source/" + table + ".csv", table + "_source");
                checker.loadCsvIntoTable(conn, "files/target/" + table + ".csv", table + "_target");

                totalDiffs += checker.compare(conn, table);
            }

            if (totalDiffs > 0) {
                fail("נמצאו " + totalDiffs + " הבדלים במספר טבלאות. ראה differences.txt");
            }
        }
    }
}
