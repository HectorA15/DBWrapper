package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hectora15.util.JDBCInterpreter;

public class DeleteViewController {

    @FXML private ComboBox<String> deleteTableCombo;
    @FXML private TextArea deleteWhereArea; // ej: "id = 5"
    @FXML private Button deleteButton;

    private JDBCInterpreter interpreter;

    /**
     * Initializes the view.
     * Loads the tables into the ComboBox and sets up the delete button action.
     * Called automatically by JavaFX when the FXML is loaded.
     */
    @FXML
    public void initialize() {
        deleteButton.setOnAction(e -> onDeleteClick());
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
        String where = deleteWhereArea.getText().trim();

        if (table == null) {
            showAlert(Alert.AlertType.WARNING, "Sin tabla", "Selecciona una tabla.");
            return;
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