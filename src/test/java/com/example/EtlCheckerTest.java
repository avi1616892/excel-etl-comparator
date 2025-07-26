package com.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.fail;

public class EtlCheckerTest {

    @Test
    void testEtlComparison() throws Exception {
        EtlChecker checker = new EtlChecker();
        try (Connection conn = checker.createConnection()) {
            checker.loadCsvIntoTable(conn, "files/source.csv", "source");
            checker.loadCsvIntoTable(conn, "files/target.csv", "target");

            int diff = checker.compareTables(conn);

            if (diff > 0) {
                fail("נמצאו " + diff + " הבדלים בין source ל-target");
            }
        }
    }
}
