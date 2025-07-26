package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MultiEtlChecker {

    public void loadCsvIntoTable(Connection conn, String csvPath, String tableName) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
            // שינינו value ל-val
            stmt.executeUpdate("CREATE TABLE " + tableName + " (id INT, name VARCHAR(50), val VARCHAR(50))");

            try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    String sql = String.format(
                            "INSERT INTO %s VALUES (%s, '%s', '%s')",
                            tableName, parts[0], parts[1], parts[2]
                    );
                    stmt.executeUpdate(sql);
                }
            }
        }
    }

    public int compare(Connection conn, String table) throws Exception {
        int diffCount = 0;
        try (Statement stmt = conn.createStatement();
             FileWriter writer = new FileWriter("differences.txt", true)) { // true = Append

            writer.write("\n--- Checking table: " + table + " ---\n");

            // 1. הבדלים בערכים
            var rs = stmt.executeQuery(
                    "SELECT s.id, s.name, s.val, t.name, t.val " +
                            "FROM " + table + "_source s JOIN " + table + "_target t ON s.id = t.id " +
                            "WHERE s.name<>t.name OR s.val<>t.val"
            );
            while (rs.next()) {
                diffCount++;
                writer.write(String.format("Diff in %s for ID %d: source=(%s,%s) target=(%s,%s)%n",
                        table, rs.getInt(1),
                        rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)));
            }

            // 2. חסר ב-target
            rs = stmt.executeQuery(
                    "SELECT s.id,s.name,s.val FROM " + table + "_source s " +
                            "LEFT JOIN " + table + "_target t ON s.id=t.id WHERE t.id IS NULL"
            );
            while (rs.next()) {
                diffCount++;
                writer.write(String.format("Missing in target (%s): ID %d (%s,%s)%n",
                        table, rs.getInt(1), rs.getString(2), rs.getString(3)));
            }

            // 3. חסר ב-source
            rs = stmt.executeQuery(
                    "SELECT t.id,t.name,t.val FROM " + table + "_target t " +
                            "LEFT JOIN " + table + "_source s ON s.id=t.id WHERE s.id IS NULL"
            );
            while (rs.next()) {
                diffCount++;
                writer.write(String.format("Missing in source (%s): ID %d (%s,%s)%n",
                        table, rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        }
        return diffCount;
    }

    public Connection createConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    }
}
