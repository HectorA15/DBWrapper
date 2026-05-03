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

    /**
     * Sets the stage of this dialog. This method is called by the main application to give a reference to the dialog stage,
     * which is needed to close the dialog when the user clicks "Connect" or "Cancel
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        connectButton.setOnAction(e -> handleConnect());
        cancelButton.setOnAction(e -> handleCancel());

        // Add a listener to each input field to update the "Connect" button state whenever the user types something
        urlField.textProperty().addListener((obs, old, newVal) -> updateConnectButton());
        userField.textProperty().addListener((obs, old, newVal) -> updateConnectButton());
        passwordField.textProperty().addListener((obs, old, newVal) -> updateConnectButton());

        updateConnectButton();
    }

    /**
     * Updates the state of the "Connect" button based on the current input in the URL, username,
     * and password fields. The "Connect" button is disabled if any of these fields are empty.
     * This method is typically triggered when changes are detected in any of the input fields.
     */
    private void updateConnectButton() {
        connectButton.setDisable(
                urlField.getText().isEmpty() ||
                        userField.getText().isEmpty() ||
                        passwordField.getText().isEmpty()
        );
    }

    /**
     * Handles the "Connect" button click event.
     * Sets the connectClicked flag to true and closes the dialog stage.
     */
    private void handleConnect() {
        connectClicked = true;
        dialogStage.close();
    }

    /**
     * Handles the "Cancel" button click event.
     * Sets the connectClicked flag to false and closes the dialog stage.
     */
    private void handleCancel() {
        connectClicked = false;
        dialogStage.close();
    }

    // ======================== Getters ========================
    public boolean isConnectClicked() {return connectClicked;}
    public String getUrl() {return urlField.getText();}
    public String getUser() {return userField.getText();}
    public String getPassword() {return passwordField.getText();}


}