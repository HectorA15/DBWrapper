package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.hectora15.util.JDBCInterpreter;
import org.hectora15.util.ResultSetData;

import java.util.ArrayList;
import java.util.List;

public class InsertViewController {

    @FXML private ComboBox<String> insertTableCombo;
    @FXML private VBox insertFieldsVbox; // un TextField por columna, generado dinámicamente
    @FXML private Button insertButton;

    private JDBCInterpreter interpreter;
    private List<String> currentColumns = new ArrayList<>();

    @FXML
    public void initialize() {
        insertTableCombo.setOnAction(e -> onTableSelected());
        insertButton.setOnAction(e -> onInsertClick());
    }

    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
        loadTables();
    }

    private void loadTables() {
        try {
            insertTableCombo.getItems().setAll(interpreter.getAvailableTables());
        } catch (RuntimeException e) {
            System.err.println("InsertView: error loading tables: " + e.getMessage());
        }
    }

    private void onTableSelected() {
        String table = insertTableCombo.getValue();
        if (table == null) return;

        try {
            insertFieldsVbox.getChildren().clear();
            currentColumns.clear();

            ResultSetData data = interpreter.getTableData(table, "SELECT * FROM " + table + " WHERE 1=0");
            ResultSetData type= interpreter.getTableType(table);

            for (int i = 0; i < data.getColumnCount(); i++) {
                String col = data.getColumnName(i);
               String colDate= type.getColumnTypes(i);
               TextField typeText = new TextField();
                typeText.setPromptText(colDate);

                currentColumns.add(col);
                insertFieldsVbox.getChildren().add(new Label(col));
                insertFieldsVbox.getChildren().add(typeText);

            }
        } catch (RuntimeException e) {
            System.err.println("InsertView: error loading columns: " + e.getMessage());
        }
    }

    private void onInsertClick() {
        String table = insertTableCombo.getValue();
        if (table == null || currentColumns.isEmpty()) return;

        List<Object> values = new ArrayList<>();
        // Los TextField están en posiciones impares del VBox (0=Label, 1=TextField, 2=Label…)
        for (int i = 1; i < insertFieldsVbox.getChildren().size(); i += 2) {
            values.add(((TextField) insertFieldsVbox.getChildren().get(i)).getText());
        }

        try {
            interpreter.insert(table, String.join(",", currentColumns), values.toArray());
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Fila insertada en '" + table + "'.");
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error al insertar", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}