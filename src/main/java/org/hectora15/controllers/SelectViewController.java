package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hectora15.util.JDBCInterpreter;
import org.hectora15.util.ResultSetData;

import java.util.*;
import java.util.stream.Collectors;

public class SelectViewController {

    private final Map<String, TitledPane> columnPaneMap = new HashMap<>();
    private final Map<String, VBox> columnVboxMap = new HashMap<>();
    private final Map<String, ScrollPane> tableDataPanes = new HashMap<>();
    @FXML private VBox selectTableVbox;
    @FXML private VBox selectColumnVBox;
    @FXML private TitledPane selectTableTitled;
    private Pane tablePane;
    private JDBCInterpreter interpreter;

    /**
     * Called when the FXML is loaded. Initializes the view and prints a message to the console for debugging purposes.
     */
    @FXML
    public void initialize() {
        System.out.println("SelectViewController initialized");
    }

    /**
     * Called by DBMain when a connection is established and a JDBCInterpreter is ready.
     *
     * @param interpreter
     * @param tablePane
     */
    public void onConnectionReady(JDBCInterpreter interpreter, Pane tablePane) {
        this.interpreter = interpreter;
        this.tablePane = tablePane;
        loadAvailableTables();
    }

    /**
     * Loads the available tables from the database and creates a checkbox for each table.
     * If a table was previously selected, it remains checked and its state is reconstructed.
     */
    public void loadAvailableTables() {
        Set<String> previouslySelected = new HashSet<>(columnPaneMap.keySet());

        new HashSet<>(previouslySelected).forEach(this::uncheckedCleanup);

        try {
            selectTableVbox.getChildren().clear();
            List<String> tables = interpreter.getAvailableTables();

            for (String table : tables) {
                CheckBox cb = new CheckBox(table);

                cb.setSelected(previouslySelected.contains(table));

                cb.selectedProperty().addListener((obs, was, isSelected) -> {
                    if (isSelected) onTableChecked(table);
                    else            onTableUnchecked(table);
                });

                if (cb.isSelected()) onTableChecked(table);

                selectTableVbox.getChildren().add(cb);
            }
        } catch (Exception e) {
            System.err.println("Error loading tables: " + e.getMessage());
        }
    }



    /**
     * Called when a table checkbox is checked. It creates a TitledPane with a ScrollPane containing checkboxes for each column of the table.
     *
     * @param tableName
     */
    private void onTableChecked(String tableName) {
        if (columnPaneMap.containsKey(tableName)) return;

        VBox columnsVbox = new VBox(4);
        columnsVbox.setPadding(new Insets(6, 8, 6, 8));
        try {
            ResultSetData meta = interpreter.getTableData(
                    tableName, "SELECT * FROM " + tableName + " WHERE 1=0");
            for (int i = 0; i < meta.getColumnCount(); i++) {
                CheckBox colCb = new CheckBox(meta.getColumnName(i));
                colCb.selectedProperty().addListener((obs, old, sel) ->
                        refreshTableData(tableName));
                columnsVbox.getChildren().add(colCb);
            }
        } catch (Exception e) {
            columnsVbox.getChildren().add(new Label("Error: " + e.getMessage()));
        }
        columnVboxMap.put(tableName, columnsVbox);

        ScrollPane colScroll = new ScrollPane(columnsVbox);
        colScroll.setFitToWidth(true);
        colScroll.setPrefHeight(150.0);

        TitledPane colPane = new TitledPane(tableName, colScroll);
        colPane.setAnimated(false);
        columnPaneMap.put(tableName, colPane);
        selectColumnVBox.getChildren().add(colPane);


        ScrollPane dataScroll = new ScrollPane();
        dataScroll.setFitToWidth(true);
        dataScroll.setPrefHeight(280.0);
        tableDataPanes.put(tableName, dataScroll);
        tablePane.getChildren().add(dataScroll);

        refreshTableData(tableName);
    }


    /**
     * Called when a table checkbox is unchecked.
     * It removes the corresponding TitledPane and ScrollPane from the view and internal maps.
     * @param tableName
     */
    private void onTableUnchecked(String tableName) {
        uncheckedCleanup(tableName);
    }

    /**
     * Removes the TitledPane for the columns and the ScrollPane for the data of the specified table from the view and internal maps.
     * @param tableName
     */
    private void uncheckedCleanup(String tableName) {
        TitledPane colPane = columnPaneMap.remove(tableName);
        if (colPane != null) selectColumnVBox.getChildren().remove(colPane);
        columnVboxMap.remove(tableName);

        ScrollPane dataPane = tableDataPanes.remove(tableName);
        if (dataPane != null && tablePane != null)
            tablePane.getChildren().remove(dataPane);
    }

    /**
     * Refreshes the table data by executing a SELECT query based on the selected columns.
     * If no columns are selected, it defaults to SELECT *.
     * If the table is not found or there is an error executing the query, it prints an error message to the console.
     * @param tableName
     */
    private void refreshTableData(String tableName) {
        VBox vbox = columnVboxMap.get(tableName);
        if (vbox == null) return;

        List<String> selectedCols = vbox.getChildren().stream()
                .filter(n -> n instanceof CheckBox && ((CheckBox) n).isSelected())
                .map(n -> ((CheckBox) n).getText())
                .collect(Collectors.toList());

        String query = selectedCols.isEmpty()
                ? "SELECT * FROM " + tableName
                : "SELECT " + String.join(", ", selectedCols) + " FROM " + tableName;

        try {
            ResultSetData data = interpreter.getTableData(tableName, query);
            ScrollPane sp = tableDataPanes.get(tableName);
            if (sp != null) sp.setContent(buildTable(tableName, data));
        } catch (Exception e) {
            System.err.println("Error refreshing " + tableName + ": " + e.getMessage());
        }

    }

    /**
     * Builds a VBox containing a label with the table name and a GridPane with the table data.
     * The first row of the GridPane contains the column headers, and the subsequent rows contain the data values.
     * If there are no rows, it shows a label indicating that there are no rows.
     * @param tableName
     * @param data
     * @return
     */
    private VBox buildTable(String tableName, ResultSetData data) {
        Label tableLabel = new Label("  " + tableName);
        tableLabel.setMaxWidth(Double.MAX_VALUE);
        tableLabel.getStyleClass().add("table-title");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));

        // headers
        for (int col = 0; col < data.getColumnCount(); col++) {
            Label h = new Label(data.getColumnName(col));
            h.setMaxWidth(Double.MAX_VALUE);
            h.getStyleClass().add("table-header-cell");
            GridPane.setHgrow(h, Priority.ALWAYS);
            grid.add(h, col, 0);
        }

        // rows
        for (int row = 0; row < data.getRowCount(); row++) {
            String styleClass = (row % 2 == 0) ? "table-cell-even" : "table-cell-odd";
            for (int col = 0; col < data.getColumnCount(); col++) {
                Label cell = new Label(data.getValue(row, col));
                cell.setMaxWidth(Double.MAX_VALUE);
                cell.getStyleClass().add(styleClass);
                GridPane.setHgrow(cell, Priority.ALWAYS);
                grid.add(cell, col, row + 1);
            }
        }

        if (data.getRowCount() == 0) {
            Label empty = new Label("  (empty)");
            empty.getStyleClass().add("table-empty-label");
            grid.add(empty, 0, 1);
        }

        VBox widget = new VBox(0, tableLabel, grid);
        widget.getStyleClass().add("table-widget");
        return widget;
    }


}