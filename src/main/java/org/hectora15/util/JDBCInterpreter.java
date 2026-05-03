package org.hectora15.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interprete the SQL commands.
 *
 */
public class JDBCInterpreter {

    private final String url;
    private final String username;
    private final String password;
    private final Connection connect;

    // =========================== CONSTRUCTOR ===========================

    /**
     * Creates a new JDBCInterpreter and establishes a connection to the database.
     * @param url
     * @param username
     * @param password
     */
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

    /**
     * Creates a new table with the given name and columns definition.
     * The columns parameter should be a comma-separated list of column definitions, e.g. "id INT PRIMARY KEY, name VARCHAR(255)".
     * If the table already exists, it will not be recreated (due to IF NOT EXISTS).
     * @param tableName
     * @param columns
     */
    public void createTable(String tableName, String columns) {
        validateTableName(tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the table with the given name. If the table does not exist, it will throw an error.
     * @param tableName
     */
    public void deleteTable(String tableName) {
        validateTableName(tableName);
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate("DROP TABLE " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting table: " + e.getMessage(), e);
        }
    }

    /**
     * Inserts a new row into the specified table. The columns parameter should be a comma-separated list of column names, e.g. "name, age".
     * The values parameter should be an array of objects corresponding to the columns, e.g. new Object[]{"Alice", 30}.
     * If the table does not exist or the columns do not match, it will throw an error.
     * @param tableName
     * @param columns
     * @param values
     */
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
     * Executes the given SQL update command (e.g. UPDATE, DELETE). It does not return any result. If the command fails, it will throw an error.
     * @param sql
     */
    public void executeUpdate(String sql) {
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update: " + e.getMessage(), e);
        }
    }

    // =========================== QUERIES ===========================

    /**
     * Returns a list of all available table names in the current database.
     * It uses the DatabaseMetaData to fetch the tables. If there is an error, it will throw an exception.
     * @return
     */
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

    /**
     * Returns the data of the specified table as a ResultSetData object, which contains the column names and rows.
     * @param tableName
     * @param query
     * @return
     */
    public ResultSetData getTableData(String tableName, String query) {
        validateTableName(tableName);
        List<String> columnNames = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                columnNames.add(meta.getColumnName(i));
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
        return new ResultSetData(columnNames, rows);
    }

    // =========================== UTILS ===========================

    /**
     * Validates the table name to ensure it is not null, not empty, and matches a simple pattern
     * (starts with a letter or underscore, followed by letters, digits, or underscores).
     * @param tableName
     */
    public void validateTableName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        }
        if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }

    /**
     * Closes the database connection. If there is an error while closing, it will throw a RuntimeException.
     */
    public void closeConnection() {
        try {
            connect.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection: " + e.getMessage(), e);
        }
    }
}
