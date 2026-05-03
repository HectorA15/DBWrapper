package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.hectora15.util.JDBCInterpreter;

public class DBController {

    @FXML private Label actualMethodLabel;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private StackPane stackCreate;

    // tablePane vive en UI.fxml (center del BorderPane) — se pasa a SelectViewController
    @FXML private Pane tablePane;

    /*
     * Convención JavaFX fx:include:
     *   <fx:include fx:id="createView"  source="CreateView.fxml"/>
     *   → inyecta el controller en el campo "createViewController"  (fx:id + "Controller")
     */
    @FXML private SelectViewController selectViewController;
    @FXML private InsertViewController insertViewController;
    @FXML private CreateViewController createViewController;
    @FXML private DeleteViewController deleteViewController;

    private final String[] viewNames = {"CREATE", "INSERT", "SELECT", "DELETE"};
    private int currentViewIndex = 0;

    @FXML
    public void initialize() {
        nextButton.setOnAction(e -> showNextView());
        previousButton.setOnAction(e -> showPreviousView());
        showView(0);
    }

    /** Llamado por DBMain tras una conexión exitosa. */
    public void onConnectionReady(JDBCInterpreter interpreter) {
        // SelectViewController necesita tablePane además del intérprete
        if (selectViewController != null)
            selectViewController.onConnectionReady(interpreter, tablePane);
        if (insertViewController != null)
            insertViewController.onConnectionReady(interpreter);
        if (createViewController != null)
            createViewController.onConnectionReady(interpreter);
        if (deleteViewController != null)
            deleteViewController.onConnectionReady(interpreter);
    }

    private void showNextView() {
        currentViewIndex = (currentViewIndex + 1) % stackCreate.getChildren().size();
        showView(currentViewIndex);
    }

    private void showPreviousView() {
        currentViewIndex = (currentViewIndex - 1 + stackCreate.getChildren().size()) % stackCreate.getChildren().size();
        showView(currentViewIndex);
    }

    private void showView(int index) {
        for (int i = 0; i < stackCreate.getChildren().size(); i++)
            stackCreate.getChildren().get(i).setVisible(i == index);
        actualMethodLabel.setText(viewNames[index]);
    }
}