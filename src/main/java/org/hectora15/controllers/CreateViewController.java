package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.hectora15.ui.DBMain;
import org.hectora15.util.JDBCInterpreter;

import java.sql.Connection;

public class CreateViewController {

    private VBox createView;

    @FXML private TextField createTableField;
    @FXML private TextField createQuantityField;
    @FXML private Button createButton;

    private JDBCInterpreter interpreter;
    private Connection connect;

    public CreateViewController(VBox createView) {
        this.createView = createView;
    }

    public void initialize() {
        System.out.println("CreateViewController initialized");


        setupListeners();
    }

    private void setupListeners() {
        // TODO: createButton.setOnAction(e -> onCreateClick());
    }

    private void onCreateClick() {
        String tableName = createTableField.getText().trim();
        String columns = "id INT PRIMARY KEY, nombre VARCHAR(255), dorsal INT";

        if (tableName.isEmpty()) {
            showNotify("Error", "Debes ponerle un nombre a la tabla.");
            return;
        }

        try {
            DBMain.jdbcInterpreter.createTable(tableName, columns);
            showNotify("Éxito", "Tabla '" + tableName + "' creada correctamente.");
        } catch (Exception e) {
            showNotify("Error al crear", e.getMessage());
        }
    }

    private void showNotify(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadJDBCInterpreter() {
        String url = DBMain.jdbcInterpreter.getUrl();
        String user = DBMain.jdbcInterpreter.getUsername();
        String password = DBMain.jdbcInterpreter.getPassword();
        interpreter = new JDBCInterpreter(url,user,password);
        connect = interpreter.getConnect();
    }


    public void onConnectionReady() {
        System.out.println("InsertViewController: Connection is ready, loading tables...");

    }
}