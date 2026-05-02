package org.hectora15.ui;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.hectora15.util.JDBCInterpreter;

import java.util.Optional;

public class DBMain extends Application {

    private Stage primaryStage;
    public static JDBCInterpreter jdbcInterpreter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        Parent root = FXMLLoader.load(getClass().getResource("/UI.fxml"));
        root.setDisable(true);

        Scene scene = new Scene(root);
        primaryStage.setTitle("DB Wrapper");
        primaryStage.setScene(scene);
        primaryStage.show();

        showLoginAndConnect(root);
    }

    private void showLoginAndConnect(Parent root) {
        Optional<LoginDialog.Credentials> result = LoginDialog.showLoginDialog();

        if (result.isPresent()) {
            LoginDialog.Credentials creds = result.get();
            System.out.println("Connecting to " + creds.url);
            System.out.println("Username: " + creds.user);

            try {
                jdbcInterpreter = new JDBCInterpreter(creds.url, creds.user, creds.password);
                System.out.println("Connection successful");

                root.setDisable(false);

            } catch (RuntimeException e) {
                showError("Connection Error : " + e.getMessage());
                showLoginAndConnect(root);
            }

        } else {
            System.out.println("Login cancelled by user");
            System.exit(0);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error in Conection");
        alert.setHeaderText("Failed to connect to the database");
        alert.setContentText(message);
        alert.showAndWait();
    }



}