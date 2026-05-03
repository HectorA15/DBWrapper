package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.hectora15.util.JDBCInterpreter;

public class CreateViewController {

    @FXML private TextField createTableField;
    @FXML private TextField createColumnsField; // ej: "id INT PRIMARY KEY, name VARCHAR(100)"
    @FXML private Button createButton;

    private JDBCInterpreter interpreter;

    @FXML
    public void initialize() {
        createButton.setOnAction(e -> onCreateClick());
    }

    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    private void onCreateClick() {
        String tableName = createTableField.getText().trim();
        String columns   = createColumnsField.getText().trim();

        if (tableName.isEmpty() || columns.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Ingresa nombre de tabla y columnas.");
            return;
        }

        try {
            interpreter.createTable(tableName, columns);
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Tabla '" + tableName + "' creada correctamente.");
            createTableField.clear();
            createColumnsField.clear();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error al crear tabla", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}