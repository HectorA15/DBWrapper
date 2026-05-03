package org.hectora15.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCInterpreter {

    private final String url;
    private final String username;
    private final String password;
    private final Connection connect;

    // =========================== CONSTRUCTOR ===========================
    public JDBCInterpreter(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            this.connect = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect: " + e.getMessage(), e);
        }
    }

    // =========================== GETTERS ===========================
    public String getUrl()      { return url; }
    public String getUsername() { return username; }
    public Connection getConnect() { return connect; }

    // =========================== DDL / DML ===========================

    public void createTable(String tableName, String columns) {
        validateTableName(tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table: " + e.getMessage(), e);
        }
    }

    public void deleteTable(String tableName) {
        validateTableName(tableName);
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate("DROP TABLE " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting table: " + e.getMessage(), e);
        }
    }

    public void insert(String tableName, String columns, Object[] values) {
        validateTableName(tableName);
        String placeholders = "?,".repeat(values.length);
        placeholders = placeholders.substring(0, placeholders.length() - 1);
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting: " + e.getMessage(), e);
        }
    }

    /**
     * Ejecuta cualquier sentencia UPDATE / DELETE / DDL que no devuelva ResultSet.
     * Útil para DELETE con WHERE personalizado desde la UI.
     */
    public void executeUpdate(String sql) {
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update: " + e.getMessage(), e);
        }
    }

    // =========================== QUERIES ===========================

    public List<String> getAvailableTables() {
        List<String> tableNames = new ArrayList<>();
        try {

            String currentCatalog = connect.getCatalog();
            System.out.println("Current catalog: " + currentCatalog);

            ResultSet rs = connect.getMetaData().getTables(currentCatalog, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching table names: " + e.getMessage(), e);
        }

        return tableNames;
    }

    public ResultSetData getTableData(String tableName, String query) {
        validateTableName(tableName);
        List<String> columnNames = new ArrayList<>();
        List<String> columnTypes= new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                columnNames.add(meta.getColumnName(i));
              columnTypes.add( meta.getColumnTypeName(i));
            }
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= cols; i++) {
                    String val = rs.getString(i);
                    row.add(val != null ? val : "");
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching table data: " + e.getMessage(), e);
        }
        return new ResultSetData(columnNames, rows, columnTypes);
    }

    // =========================== UTILS ===========================

    public void validateTableName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        }
        if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }

    public void closeConnection() {
        try {
            connect.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection: " + e.getMessage(), e);
        }
    }
}
