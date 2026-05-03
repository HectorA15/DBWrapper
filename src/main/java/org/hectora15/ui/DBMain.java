package org.hectora15.ui;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hectora15.controllers.DBController;
import org.hectora15.controllers.LoginDialogController;
import org.hectora15.util.JDBCInterpreter;

import java.io.IOException;

public class DBMain extends Application {

    private Stage primaryStage;
    private DBController dbController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI.fxml"));
        Parent root = loader.load();
        dbController = loader.getController();

        root.setDisable(true);

        primaryStage.setTitle("DB Wrapper");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        showLoginAndConnect(root);
    }

    private void showLoginAndConnect(Parent root) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginDialog.fxml"));
            Parent loginRoot = loader.load();
            LoginDialogController loginCtrl = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Connect to Database");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setScene(new Scene(loginRoot));
            loginCtrl.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (!loginCtrl.isConnectClicked()) {
                System.exit(0);
                return;
            }

            try {
                JDBCInterpreter interpreter = new JDBCInterpreter(
                        loginCtrl.getUrl(),
                        loginCtrl.getUser(),
                        loginCtrl.getPassword()
                );
                root.setDisable(false);
                dbController.onConnectionReady(interpreter); // <-- pasa el intérprete aquí

            } catch (RuntimeException e) {
                showError("Connection Error: " + e.getMessage());
                showLoginAndConnect(root); // reintenta el login
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error in Connection");
        alert.setHeaderText("Failed to connect to the database");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}