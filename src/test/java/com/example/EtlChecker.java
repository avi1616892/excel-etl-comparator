package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class EtlChecker {

    public void loadCsvIntoTable(Connection conn, String csvPath, String tableName) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
            stmt.executeUpdate("CREATE TABLE " + tableName + " (id INT, name VARCHAR(50), age INT)");

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
                            "INSERT INTO %s VALUES (%s, '%s', %s)",
                            tableName, parts[0], parts[1], parts[2]
                    );
                    stmt.executeUpdate(sql);
                }
            }
        }
    }

    public int compareTables(Connection conn) throws Exception {
        int diffCount = 0;

        try (FileWriter writer = new FileWriter("differences.txt");
             Statement stmt = conn.createStatement()) {

            // 1. רשומות שקיימות בשני הצדדים אבל יש ערכים שונים
            var rs = stmt.executeQuery(
                    "SELECT s.id, s.name, s.age, t.name, t.age " +
                            "FROM source s JOIN target t ON s.id = t.id " +
                            "WHERE s.name <> t.name OR s.age <> t.age"
            );
            while (rs.next()) {
                diffCount++;
                int id = rs.getInt(1);
                String sName = rs.getString(2);
                int sAge = rs.getInt(3);
                String tName = rs.getString(4);
                int tAge = rs.getInt(5);

                String line = String.format(
                        "Difference for ID %d: source=(%s,%d) target=(%s,%d)%n",
                        id, sName, sAge, tName, tAge
                );
                writer.write(line);
            }

            // 2. רשומות שחסרות ב-target
            rs = stmt.executeQuery(
                    "SELECT s.id, s.name, s.age " +
                            "FROM source s LEFT JOIN target t ON s.id = t.id " +
                            "WHERE t.id IS NULL"
            );
            while (rs.next()) {
                diffCount++;
                int id = rs.getInt(1);
                String sName = rs.getString(2);
                int sAge = rs.getInt(3);

                String line = String.format(
                        "Missing in target: ID %d (%s,%d)%n",
                        id, sName, sAge
                );
                writer.write(line);
            }

            // 3. רשומות שחסרות ב-source (קיימות רק ב-target)
            rs = stmt.executeQuery(
                    "SELECT t.id, t.name, t.age " +
                            "FROM target t LEFT JOIN source s ON s.id = t.id " +
                            "WHERE s.id IS NULL"
            );
            while (rs.next()) {
                diffCount++;
                int id = rs.getInt(1);
                String tName = rs.getString(2);
                int tAge = rs.getInt(3);

                String line = String.format(
                        "Missing in source: ID %d (%s,%d)%n",
                        id, tName, tAge
                );
                writer.write(line);
            }
        }

        return diffCount;
    }


    public Connection createConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    }
}
