package org.hectora15.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.util.Optional;

public class LoginDialog {

    public static class Credentials {
        public String url;
        public String user;
        public String password;

        public Credentials(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }




    // =========================== METHODS ===========================
    public static Optional<Credentials> showLoginDialog() {
        Dialog<Credentials> dialog = new Dialog<>();
        dialog.setTitle("Connect to the BD");
        dialog.setResizable(false);

        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType connectButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(connectButton, cancelButton);




        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField urlField = new TextField();
        urlField.setPromptText("jdbc:mysql://localhost:3306/dbname");
        urlField.setPrefWidth(300);

        TextField userField = new TextField();
        userField.setPromptText("root");
        userField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password");
        passwordField.setPrefWidth(300);

        grid.add(new Label("URL:"), 0, 0);
        grid.add(urlField, 1, 0);

        grid.add(new Label("User:"), 0, 1);
        grid.add(userField, 1, 1);

        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(connectButton);
        okButton.setDisable(true);

        urlField.textProperty().addListener((obs, old, newVal) ->
                updateButtonState(okButton, urlField, userField, passwordField)
        );
        userField.textProperty().addListener((obs, old, newVal) ->
                updateButtonState(okButton, urlField, userField, passwordField)
        );
        passwordField.textProperty().addListener((obs, old, newVal) ->
                updateButtonState(okButton, urlField, userField, passwordField)
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButton) {
                return new Credentials(
                        urlField.getText(),
                        userField.getText(),
                        passwordField.getText()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static void updateButtonState(Button button, TextField url, TextField user, PasswordField password) {
        button.setDisable(
                url.getText().isEmpty() ||
                        user.getText().isEmpty() ||
                        password.getText().isEmpty()
        );
    }
}