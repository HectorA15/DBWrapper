package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hectora15.util.JDBCInterpreter;

public class DeleteViewController {

    @FXML private ComboBox<String> deleteTableCombo;
    @FXML private TextArea deleteWhereArea; // ej: "id = 5"
    @FXML private Button deleteButton;

    private JDBCInterpreter interpreter;

    @FXML
    public void initialize() {
        deleteButton.setOnAction(e -> onDeleteClick());
    }

    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
        loadTables();
    }

    private void loadTables() {
        try {
            deleteTableCombo.getItems().setAll(interpreter.getAvailableTables());
        } catch (RuntimeException e) {
            System.err.println("DeleteView: error loading tables: " + e.getMessage());
        }
    }

    private void onDeleteClick() {
        String table = deleteTableCombo.getValue();
        String where = deleteWhereArea.getText().trim();

        if (table == null) {
            showAlert(Alert.AlertType.WARNING, "Sin tabla", "Selecciona una tabla.");
            return;
        }

        // Confirmación antes de borrar
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("¿Eliminar registros de '" + table + "'?");
        confirm.setContentText(where.isEmpty()
                ? "⚠ Sin cláusula WHERE — se borrarán TODOS los registros."
                : "WHERE " + where);
        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            try {
                String sql = where.isEmpty()
                        ? "DELETE FROM " + table
                        : "DELETE FROM " + table + " WHERE " + where;
                interpreter.executeUpdate(sql);
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Registros eliminados.");
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Error al eliminar", e.getMessage());
            }
        });
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}