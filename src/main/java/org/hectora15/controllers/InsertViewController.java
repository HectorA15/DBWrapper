package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class InsertViewController {

    private VBox insertView;

    @FXML private ComboBox<String> insertTableCombo;
    @FXML private Button insertButton;

    // TODO: por cada columna que tenga la tabla seleccionada deben aparecer dos campos por cada columna en el GRIDPANE

    public InsertViewController(VBox insertView) {
        this.insertView = insertView;
    }

    public void initialize() {
        System.out.println("InsertViewController initialized");

        // TODO: Cargar tablas en insertTableCombo desde BD
        // TODO: Al seleccionar tabla, generar TextFields para cada columna
        // TODO: Conectar botón insertButton a JDBCInterpreter.insert()

        setupListeners();
    }

    private void setupListeners() {
        // TODO: insertTableCombo.setOnAction(e -> onTableSelected());
        // TODO: insertButton.setOnAction(e -> onInsertClick());
    }

    // TODO: private void onTableSelected() { }
    // TODO: private void onInsertClick() { }
}