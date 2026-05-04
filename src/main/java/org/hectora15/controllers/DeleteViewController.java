package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hectora15.util.JDBCInterpreter;

public class DeleteViewController {

    @FXML private ComboBox<String> deleteTableCombo;
    @FXML private TextArea deleteWhereArea; // ej: "id = 5"
    @FXML private Button deleteButton;
@FXML private Button deleteElementButton;
    private JDBCInterpreter interpreter;

    /**
     * Initializes the view.
     * Loads the tables into the ComboBox and sets up the delete button action.
     * Called automatically by JavaFX when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        deleteButton.setOnAction(e -> onDeleteClick());
        deleteElementButton.setOnAction(e -> onDelectElementSelected());
    }

    /**
     * Called by DBMain when a connection is established and a JDBCInterpreter is ready.
     * @param interpreter
     */
    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
        loadTables();
    }

    /**
     * Loads the available tables from the database and populates the ComboBox. If there's an error, it prints it to the console.
     */
    private void loadTables() {
        try {
            deleteTableCombo.getItems().setAll(interpreter.getAvailableTables());
        } catch (RuntimeException e) {
            System.err.println("DeleteView: error loading tables: " + e.getMessage());
        }
    }

    /**
     * Called when the delete button is clicked.
     * It retrieves the selected table and the WHERE clause from the user input, validates them, and then executes the DELETE statement using the JDBCInterpreter.
     * If there's an error, it shows an alert with the error message.
     */
    private void onDeleteClick() {
        String table = deleteTableCombo.getValue();

        if (table == null) {
            showAlert(Alert.AlertType.WARNING, "Sin tabla", "Selecciona una tabla.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("¡ADVERTENCIA!");
        confirm.setHeaderText("¿Seguro de eliminar la tabla '" + table + "'?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                interpreter.deleteTable(table);
                showAlert(Alert.AlertType.INFORMATION, "Tabla Eliminada", "La tabla '" + table + "' ha sido borrada");
                deleteTableCombo.setValue(null);
                loadTables();
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Error de SQL", e.getMessage());
            }
        }

    }
    private void onDelectElementSelected(){
        String table = deleteTableCombo.getValue();
        String where = deleteWhereArea.getText().trim();

        if (table == null || where.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Faltan datos", "Selecciona tabla");
            return;
        }

        if (confirmAction("Confirmar", "¿Borrar registros de " + table + " donde " + where + "?")) {
            try {
                String sql = "DELETE FROM " + table + " WHERE " + where;
                interpreter.executeUpdate(sql);
                showAlert(Alert.AlertType.INFORMATION, "Exito", "Registros eliminados.");
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    
    }

    private boolean confirmAction(String header, String content) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText(header);
        a.setContentText(content);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
    public void refreshData() {
        if (deleteWhereArea != null) {
            deleteWhereArea.clear();
        }
        if (deleteTableCombo != null) {
            deleteTableCombo.getSelectionModel().clearSelection();
            deleteTableCombo.setValue(null);
        }
        if (this.interpreter != null) {
            loadTables();
        }
    }

    /**
     * Shows an alert with the specified type, header, and content. This is a helper method to avoid repeating code when showing alerts.
     * @param type
     * @param header
     * @param content
     */
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}