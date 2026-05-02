package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class DeleteViewController {

    private VBox deleteView;

    @FXML private ComboBox<String> deleteTableCombo;
    @FXML private TextArea deleteWhereArea;
    @FXML private Button deleteButton;

    public DeleteViewController(VBox deleteView) {
        this.deleteView = deleteView;
    }

    public void initialize() {
        System.out.println("DeleteViewController initialized");

        // TODO: Cargar tablas disponibles en deleteTableCombo
        // TODO: Agregar FilteredComboBox (buscar tablas escribiendo)
        // TODO: Validar WHERE clause antes de ejecutar
        // TODO: Conectar botón deleteButton a JDBCInterpreter.delete()

        setupListeners();
    }

    private void setupListeners() {
        // TODO: deleteTableCombo.setOnAction(e -> onTableSelected());
        // TODO: deleteButton.setOnAction(e -> onDeleteClick());
    }

    // TODO: private void onTableSelected() { }
    // TODO: private void onDeleteClick() { }
    // TODO: private boolean validateWhereClause(String where) { }
}