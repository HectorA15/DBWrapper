package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CreateViewController {

    private VBox createView;

    @FXML private TextField createTableField;
    @FXML private TextField createQuantityField;
    @FXML private Button createButton;

    public CreateViewController(VBox createView) {
        this.createView = createView;
    }

    public void initialize() {
        System.out.println("CreateViewController initialized");

        // TODO: Cargar componentes FXML
        // TODO: Generar campos dinámicamente según Quantity
        // TODO: Conectar botón createButton a JDBCInterpreter.createTable()

        setupListeners();
    }

    private void setupListeners() {
        // TODO: createButton.setOnAction(e -> onCreateClick());
    }

    // TODO: private void onCreateClick() { }
}