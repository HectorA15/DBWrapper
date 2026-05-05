package org.hectora15.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCInterpreter {

    private final String url;
    private final String username;
    private final String password;
    private final Connection connect;

    /**
     * Initializes a new instance of the JDBCInterpreter class and establishes
     * a connection to the database using the provided credentials.
     *
     * @param url the database URL to which the connection will be established
     * @param username the username for the database authentication
     * @param password the password for the database authentication
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

    public String getUrl()         { return url; }
    public String getUsername()    { return username; }
    public Connection getConnect() { return connect; }



    /**
     * Creates a new table in the database with the specified name and column definitions.
     * This method validates the table name before generating and executing the SQL
     * statement to create the table. If an error occurs during execution, a
     * RuntimeException is thrown with the relevant details.
     *
     * @param tableName the name of the table to be created; must be a non-null,
     *                  non-empty string that matches a valid table name pattern.
     * @param columnDefinition a comma-separated string defining the table's columns
     *                         and their data types. Must be a valid SQL column definition string*/
    public void createTable(String tableName, String columnDefinition) {
        validateTableName(tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnDefinition + ")";
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the specified table from the connected database. This method first validates
     * the provided table name to ensure it complies with naming conventions. If any error
     * occurs while executing the SQL DROP TABLE statement, a RuntimeException is thrown
     * with details about the exception.
     *
     * @param tableName the name of the table to be deleted; must be a valid, non-null,
     *                  and non-empty string that adheres to database naming conventions.
     * @throws IllegalArgumentException if the table name is null, empty, or invalid.
     * @throws RuntimeException if an SQL exception occurs during the deletion process.
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
     * Inserts data into the specified table with the given column names and values.
     *
     * @param tableName the name of the table where the data will be inserted; must be non-null
     *                  and match a valid table name pattern.
     * @param columns the comma-separated string of column names corresponding to the data being inserted.
     * @param values an array of values to insert into the specified columns; the size must match
     *               the number of columns provided.
     * @throws IllegalArgumentException if the table name is invalid or null.
     * @throws RuntimeException if an SQL exception occurs during the insertion process.
     */
    public void insert(String tableName, String columns, Object[] values) {
        validateTableName(tableName);
        String placeholders = "?,".repeat(values.length);
        placeholders = placeholders.substring(0, placeholders.length() - 1);
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) pstmt.setObject(i + 1, values[i]);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting: " + e.getMessage(), e);
        }
    }

    /**
     * Executes the given SQL statement, which must be an SQL Data Manipulation Language (DML)
     * statement such as INSERT, UPDATE, or DELETE. The SQL statement is executed without
     * using prepared statements, and any SQL exception encountered during execution will
     * result in a RuntimeException being thrown.
     *
     * @param sql the SQL statement to be executed. It must be a valid, non-null SQL DML
     *            string that conforms to the syntax and rules of the underlying database.
     * @throws RuntimeException if an SQL exception occurs during statement execution.
     */
    public void executeUpdate(String sql) {
        try (Statement stmt = connect.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update: " + e.getMessage(), e);
        }
    }

    /**
     * Updates rows in the specified table by constructing and executing an SQL UPDATE statement.
     * This method uses a prepared statement to safely set the values in the SET and WHERE clauses
     * to prevent SQL injection attacks. The table name is validated before constructing the SQL
     * statement. If an error occurs during execution, a RuntimeException is thrown.
     *
     * @param tableName the name of the table to be updated; must be a non-null, non-empty string
     *                  and match a valid table name pattern.
     * @param setClause the SQL SET clause specifying the columns to update and their new values.
     *                  Placeholders (?) should be used for parameterized values.
     * @param whereClause the SQL WHERE clause specifying the condition for updating rows.
     *                    Placeholders (?) should be used for parameterized values.
     * @param values an array of objects representing the values to replace the placeholders in
     *               the SET and WHERE clauses. The order of elements must correspond to the
     *               order of placeholders in the SQL query.
     * @throws IllegalArgumentException if the table name is null, empty, or invalid.
     * @throws RuntimeException if an SQL exception occurs during the update operation.
     */
    public void update(String tableName, String setClause, String whereClause, Object[] values) {
        validateTableName(tableName);
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) pstmt.setObject(i + 1, values[i]);
            int affected = pstmt.executeUpdate();
            System.out.println("UPDATE executing (" + affected + " rows): " + sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating: " + e.getMessage(), e);
        }
    }

    // =========================== QUERIES ===========================

    /**
     * Retrieves a list of available table names from the connected database.
     * The method queries the database metadata to fetch all tables visible in the current catalog.
     *
     * @return a list of strings containing the names of all tables in the connected database.
     *         If no tables are found, an empty list is returned.
     * @throws RuntimeException if an SQL exception occurs during the retrieval process.
     */
    public List<String> getAvailableTables() {
        List<String> tableNames = new ArrayList<>();
        try {
            String catalog = connect.getCatalog();
            ResultSet rs = connect.getMetaData().getTables(catalog, null, "%", new String[]{"TABLE"});
            while (rs.next()) tableNames.add(rs.getString("TABLE_NAME"));
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching table names: " + e.getMessage(), e);
        }
        return tableNames;
    }

    /**
     * Retrieves data from the specified table by executing the provided SQL query.
     * The method validates the table name and then executes the query to fetch data.
     * The resulting table data, including column names and rows, is wrapped in a {@code ResultSetData} object.
     *
     * @param tableName the name of the table to be queried; must be a valid non-null and non-empty string.
     *                  The table name is validated for correctness before execution.
     * @param query the SQL query to be executed to retrieve the data. It should be a valid SELECT statement
     *              that can be executed successfully on the specified table.
     * @return a {@code ResultSetData} object containing the column names and rows of data retrieved
     *         from the table. If no data is found, the returned object will contain an empty list
     *         for rows.
     * @throws IllegalArgumentException if the table name is null, empty, or invalid.
     * @throws RuntimeException if an SQL exception occurs during query execution.
     */
    public ResultSetData getTableData(String tableName, String query) {
        validateTableName(tableName);
        List<String> columnNames = new ArrayList<>();
        List<List<String>> rows  = new ArrayList<>();
        try (Statement stmt = connect.createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) columnNames.add(meta.getColumnName(i));
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

    /**
     * Retrieves the data types of all columns in a specified table.
     * This method executes a query to fetch the metadata of the table
     * without retrieving any actual data and extracts the column types.
     *
     * @param tableName the name of the table whose column types are to be retrieved.
     *                  Must be a valid, non-null, and non-empty string.
     * @return a {@code ResultSetData} object containing the list of column types
     *         for the specified table. If the table has no columns, the list will be empty.
     * @throws IllegalArgumentException if the table name is null, empty, or invalid.
     * @throws RuntimeException if an SQL exception occurs during the retrieval process.
     */
    public ResultSetData getTableType(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE 1=0";
        try (Statement stmt = connect.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) columnTypes.add(meta.getColumnTypeName(i));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching table types: " + e.getMessage(), e);
        }
        return new ResultSetData(columnTypes);
    }

    // =========================== UTILS ===========================

    /**
     * Validates the provided table name to ensure it is non-null, non-empty,
     * and adheres to the allowed naming convention for database tables.
     *
     * A valid table name must:
     * - Start with a letter (a-z, A-Z) or an underscore (_).
     * - Be followed by letters, numbers, or underscores (alphanumeric and underscore characters only).
     *
     * If the table name is null, empty, or invalid, an {@code IllegalArgumentException} is thrown.
     *
     * @param tableName the name of the table to validate; must be a valid, non-null, and non-empty string.
     * @throws IllegalArgumentException if the table name is null, empty, or does not match the required pattern.
     */
    public void validateTableName(String tableName) {
        if (tableName == null || tableName.isEmpty())
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))
            throw new IllegalArgumentException("Invalid table name: " + tableName);
    }

    public void closeConnection() {
        try { connect.close(); }
        catch (SQLException e) { throw new RuntimeException("Error closing connection: " + e.getMessage(), e); }
    }
}