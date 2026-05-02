package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class SelectViewController {

    private VBox selectView;

    @FXML private TitledPane selectTableTitled;
    @FXML private ScrollPane selectTableScroll;
    @FXML private TitledPane selectColumnTitled;
    @FXML private ScrollPane selectColumnScroll;
    @FXML private ScrollPane selectWhereScroll;

    public SelectViewController(VBox selectView) {
        this.selectView = selectView;
    }

    public void initialize() {
        System.out.println("SelectViewController initialized");

        // TODO: Cargar tablas disponibles en selectTableScroll (CheckBox dinámicos)
        // TODO: Al seleccionar tabla, cargar columnas disponibles
        // TODO: Generar CheckBox para WHERE dinámicamente
        // TODO: Al marcar/desmarcar, actualizar preview en tiempo real
        // TODO: Conectar a JDBCInterpreter.select()

        setupListeners();
    }

    private void setupListeners() {
        // TODO: selectTableScroll CheckBox listeners
        // TODO: selectColumnScroll CheckBox listeners
    }

    // TODO: private void loadTables() { }
    // TODO: private void loadColumns() { }
    // TODO: private void updatePreview() { }
}