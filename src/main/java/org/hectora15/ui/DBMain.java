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

    /**
     * Called when the application starts.
     * Loads the main UI and shows the login dialog to connect to the database.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        // Apply a theme
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // Load the main UI from the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI.fxml"));
        Parent root = loader.load();
        dbController = loader.getController(); // Get the controller instance

        root.setDisable(true); // Disable the UI until a connection is established

        primaryStage.setTitle("DB Wrapper");
        primaryStage.setScene(new Scene(root)); // Set the scene with the loaded UI
        primaryStage.show();

        // Show the login dialog to connect to the database
        showLoginAndConnect(root);
    }


    /**
     * Shows the login dialog to connect to the database.
     * If the connection is successful, it enables the main UI and passes the JDBCInterpreter to the DBController.
     * @param root
     */
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
                dbController.onConnectionReady(interpreter);

            } catch (RuntimeException e) {
                showError("Connection Error: " + e.getMessage());
                showLoginAndConnect(root);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an error alert with the given message.
     * @param message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error in Connection");
        alert.setHeaderText("Failed to connect to the database");
        alert.setContentText(message);
        alert.showAndWait();
    }

}