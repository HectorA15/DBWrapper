package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginDialogController {

    @FXML private TextField urlField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Button connectButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private boolean connectClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void initialize() {
        connectButton.setOnAction(e -> handleConnect());
        cancelButton.setOnAction(e -> handleCancel());

        // Validar que los campos tengan contenido
        urlField.textProperty().addListener((obs, old, newVal) -> updateConnectButton());
        userField.textProperty().addListener((obs, old, newVal) -> updateConnectButton());
        passwordField.textProperty().addListener((obs, old, newVal) -> updateConnectButton());

        updateConnectButton();
    }

    private void updateConnectButton() {
        connectButton.setDisable(
                urlField.getText().isEmpty() ||
                        userField.getText().isEmpty() ||
                        passwordField.getText().isEmpty()
        );
    }

    private void handleConnect() {
        connectClicked = true;
        dialogStage.close();
    }

    private void handleCancel() {
        connectClicked = false;
        dialogStage.close();
    }

    public boolean isConnectClicked() {
        return connectClicked;
    }

    public String getUrl() {
        return urlField.getText();
    }

    public String getUser() {
        return userField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }


}