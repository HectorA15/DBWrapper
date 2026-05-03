package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.hectora15.ui.DBMain;
import org.hectora15.util.JDBCInterpreter;
import org.hectora15.util.ResultSetData;

import java.sql.Connection;
import java.util.List;

public class SelectViewController {



    @FXML private TitledPane selectTableTitled;
    @FXML private ScrollPane selectTableScroll;
    @FXML private TitledPane selectColumnTitled;
    @FXML private ScrollPane selectColumnScroll;
    @FXML private ScrollPane selectWhereScroll;
    @FXML private Pane tablePane;


    private JDBCInterpreter interpreter;
    private Connection connect;
    private VBox selectView;

    public SelectViewController(VBox selectView) {
        this.selectView = selectView;
    }

    public void initialize() {
        System.out.println("SelectViewController initialized");


        loadAvailableTables();
        setupListeners();
    }

    private void setupListeners() {
        // TODO: selectTableScroll CheckBox listeners
        // TODO: selectColumnScroll CheckBox listeners
    }

    // TODO: private void loadTables() { }
    // TODO: private void loadColumns() { }
    // TODO: private void updatePreview() { }


    private void clearTitledPanes() {
        selectTableScroll.setContent(null);
        selectColumnScroll.setContent(null);
        selectWhereScroll.setContent(null);
    }

    public void loadAvailableTables(){
        try{
            clearTitledPanes();
            // get all tables from database
            List<String> tables = interpreter.getAvailableTables();

            for (String table : tables) {
                ResultSetData tableData = interpreter.getTableData(table, "SELECT * FROM " + table);
                drawTable(tableData, table);
            }

        }catch (Exception e){
            System.out.println("Error loading tables: " + e.getMessage());
        }
    }

    private void drawTable(ResultSetData tableData, String tableName) {
        // Check if tableData is null or empty
        if (tableData == null || tableData.getRowCount() == 0) {
            return;
        }

        // Create a grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Add column headers
        for (int col = 0; col < tableData.getColumnCount(); col++){
            Label header = new Label(tableData.getColumnName(col));
            grid.add(header, col, 0);
        }

        // Add rows
        for (int row = 0; row < tableData.getRowCount(); row++){
            for (int col = 0; col < tableData.getColumnCount(); col++){
                Label cell = new Label(tableData.getValue(row, col));
                grid.add(cell, col, row + 1);
            }
        }

        // Show the scrollpane
        tablePane.getChildren().add(grid);
    }

    private void loadJDBCInterpreter() {
        String url = DBMain.jdbcInterpreter.getUrl();
        String user = DBMain.jdbcInterpreter.getUsername();
        String password = DBMain.jdbcInterpreter.getPassword();
        interpreter = new JDBCInterpreter(url,user,password);
        connect = interpreter.getConnect();
    }

    public void onConnectionReady() {
        System.out.println("SelectViewControlelr: Connection is ready, loading tables...");
        loadAvailableTables();
    }
}