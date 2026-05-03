package org.hectora15.controllers;

import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.hectora15.util.JDBCInterpreter;
import org.hectora15.util.ResultSetData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectViewController {

    @FXML private VBox selectTableVbox;
    @FXML private VBox selectColumnVBox;

    private Pane tablePane;
    private JDBCInterpreter interpreter;

    private final Map<String, TitledPane> columnPaneMap = new HashMap<>();
    private final Map<String, VBox> columnVboxMap = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("SelectViewController initialized");
    }

    public void onConnectionReady(JDBCInterpreter interpreter, Pane tablePane) {
        this.interpreter = interpreter;
        this.tablePane   = tablePane;
        loadAvailableTables();
    }


    private void loadAvailableTables() {
        try {
            selectTableVbox.getChildren().clear();
            for (String table : interpreter.getAvailableTables()) {
                CheckBox cb = new CheckBox(table);
                cb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    if (isSelected) onTableChecked(table);
                    else            onTableUnchecked(table);
                });
                selectTableVbox.getChildren().add(cb);
            }
        } catch (Exception e) {
            System.err.println("Error loading tables: " + e.getMessage());
        }
    }


    private void onTableChecked(String tableName) {
        VBox columnsVbox = new VBox(4);
        columnsVbox.setPadding(new Insets(6, 8, 6, 8));

        try {
            ResultSetData meta = interpreter.getTableData(
                    tableName, "SELECT * FROM " + tableName + " WHERE 1=0");

            for (int i = 0; i < meta.getColumnCount(); i++) {
                CheckBox colCb = new CheckBox(meta.getColumnName(i));
                // Al cambiar cualquier columna → reconstruir la query y refrescar
                colCb.selectedProperty().addListener((obs, old, selected) ->
                        refreshTableData(tableName));
                columnsVbox.getChildren().add(colCb);
            }
        } catch (Exception e) {
            columnsVbox.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        columnVboxMap.put(tableName, columnsVbox);

        ScrollPane scroll = new ScrollPane(columnsVbox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(160.0);

        TitledPane pane = new TitledPane(tableName, scroll);
        pane.setAnimated(false);
        columnPaneMap.put(tableName, pane);
        selectColumnVBox.getChildren().add(pane);

        // Primera carga: SELECT * (ninguna columna marcada aún)
        refreshTableData(tableName);
    }

    private void onTableUnchecked(String tableName) {
        TitledPane pane = columnPaneMap.remove(tableName);
        if (pane != null) selectColumnVBox.getChildren().remove(pane);
        columnVboxMap.remove(tableName);

        if (columnPaneMap.isEmpty() && tablePane != null)
            tablePane.getChildren().clear();
    }

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

        loadTableData(tableName, query);
    }

    private void loadTableData(String tableName, String query) {
        if (tablePane == null) return;
        try {
            tablePane.getChildren().clear();
            ResultSetData data = interpreter.getTableData(tableName, query);
            drawTable(data);
        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
        }
    }


    private void drawTable(ResultSetData data) {
        if (data == null || data.getColumnCount() == 0) return;

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(6);
        grid.setPadding(new Insets(10));

        // Headers
        for (int col = 0; col < data.getColumnCount(); col++) {
            Label h = new Label(data.getColumnName(col));
            h.setStyle("-fx-font-weight: bold;");
            grid.add(h, col, 0);
        }

        // Rows
        for (int row = 0; row < data.getRowCount(); row++)
            for (int col = 0; col < data.getColumnCount(); col++)
                grid.add(new Label(data.getValue(row, col)), col, row + 1);

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);


        scroll.prefWidthProperty().bind(tablePane.widthProperty());
        scroll.prefHeightProperty().bind(tablePane.heightProperty());

        tablePane.getChildren().add(scroll);
    }
}