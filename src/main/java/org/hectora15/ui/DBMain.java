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
    public static JDBCInterpreter jdbcInterpreter;
    private DBController dbController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI.fxml"));
        Parent root = loader.load();
        dbController = loader.getController();

        root.setDisable(true);

        Scene scene = new Scene(root);
        primaryStage.setTitle("DB Wrapper");
        primaryStage.setScene(scene);
        primaryStage.show();

        showLoginAndConnect(root);
    }

    private void showLoginAndConnect(Parent root) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginDialog.fxml"));
            Parent loginRoot = loader.load();
            LoginDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Connect to Database");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setScene(new Scene(loginRoot));

            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isConnectClicked()) {
                String url = controller.getUrl();
                String user = controller.getUser();
                String password = controller.getPassword();

                System.out.println("Connecting to " + url);
                System.out.println("Username: " + user);

                try {
                    jdbcInterpreter = new JDBCInterpreter(url, user, password);
                    System.out.println("Connection successful");

                    root.setDisable(false);

                    // ← Notificar que la conexión está lista
                    dbController.onConnectionReady();

                } catch (RuntimeException e) {
                    showError("Connection Error: " + e.getMessage());
                    showLoginAndConnect(root);
                }
            } else {
                System.out.println("Login cancelled by user");
                System.exit(0);
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