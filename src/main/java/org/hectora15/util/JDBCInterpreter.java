package org.hectora15.util;

import java.sql.*;

public class JDBCInterpreter {

    private String url;
    private String username;
    private String password;


    private Connection connect;
    private Statement stmt;


    // =========================== CONSTRUCTOR ===========================
    public JDBCInterpreter (String url, String username, String password){
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
    public String getUrl() {return url;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}

     // =========================== SETTERS ===========================
    // Setters are not included to maintain immutability of connection parameters after initialization.
    /*
    public void setUrl(String url) {this.url = url;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    */

    // =========================== METHODS ===========================
    public void createTable(String tableName, String columns){
        validateTableName(tableName);
        try(Statement stmt = connect.createStatement()){

            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
            stmt.executeUpdate(sql);
            System.out.println("Table " + tableName + " created successfully.");

        }catch (SQLException e) {throw new RuntimeException("Error creating table: " + e.getMessage(), e);}
    }

    public void deleteTable(String tableName){
        validateTableName(tableName);
        try(Statement stmt = connect.createStatement()){

            String sql = "DROP TABLE " + tableName;
            stmt.executeUpdate(sql);
            System.out.println("Table " + tableName + " deleted successfully.");

        } catch (SQLException e) {throw new RuntimeException("Error deleting table: " + e.getMessage(), e);}
    }

    public void insert(String tableName, String columns, Object[] values){
        validateTableName(tableName);

        String[] placeholdersArray = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            placeholdersArray[i] = "?";
        }

        String placeholders = String.join(",", placeholdersArray);
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        try(PreparedStatement pstmt = connect.prepareStatement(sql)){

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Data inserted into " + tableName + " successfully.");

        } catch (SQLException e) {throw new RuntimeException("Error inserting: " + e.getMessage(), e);}
    }

    public void printTable(String tableName) {
        validateTableName(tableName);
        String sql = "SELECT * FROM " + tableName;

        try (Statement stmt = connect.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            int columnCount = rs.getMetaData().getColumnCount();

            while(rs.next()){
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + " | ");
                }
                System.out.println();
            }

        }catch (SQLException e) {throw new RuntimeException("Error printing table: " + e.getMessage(), e);}
    }


    // =========================== UTILS ===========================

    // Validate table name to prevent SQL injection and ensure it follows standard naming conventions
    public void validateTableName(String tableName){

        if (tableName == null ||tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        }

        if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
    }

    public void closeConnection(){
        try {
            connect.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection: " + e.getMessage(), e);
        }
    }

}
