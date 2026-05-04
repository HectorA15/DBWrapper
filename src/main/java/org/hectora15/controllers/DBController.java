package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.hectora15.util.JDBCInterpreter;

public class DBController {

    @FXML private Label actualMethodLabel;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private StackPane stackCreate;


    @FXML private VBox tablePane;


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

    /**
     * Called by DBMain when a connection is established and a JDBCInterpreter is ready.
     * Passes the interpreter to each sub-controller so they can initialize their views and perform database operations.
     * @param interpreter
     */
    public void onConnectionReady(JDBCInterpreter interpreter) {
        // SelectViewController necesita tablePane además del intérprete
        if (selectViewController != null)
            selectViewController.onConnectionReady(interpreter, tablePane);
        if (insertViewController != null)
            insertViewController.onConnectionReady(interpreter);
        if (createViewController != null) {
            createViewController.onConnectionReady(interpreter);
            createViewController.setOnTableCreatedCallback(() -> refreshAllViews());
        }

        if (deleteViewController != null)
            deleteViewController.onConnectionReady(interpreter);
    }

    private void refreshAllViews() {
        if (selectViewController != null) selectViewController.loadAvailableTables();
        if (insertViewController != null) insertViewController.loadTables();
        if (deleteViewController != null) deleteViewController.loadTables();
        System.out.println("Todas las vistas han sido actualizadas con las nuevas tablas.");
    }

    /**
     * Shows the next view in the stack (CREATE → INSERT → SELECT → DELETE → CREATE ...).
     */
    private void showNextView() {
        currentViewIndex = (currentViewIndex + 1) % stackCreate.getChildren().size();
        showView(currentViewIndex);
    }

    /**
     * Shows the previous view in the stack (CREATE → DELETE → SELECT → INSERT → CREATE ...).
     */
    private void showPreviousView() {
        currentViewIndex = (currentViewIndex - 1 + stackCreate.getChildren().size()) % stackCreate.getChildren().size();
        showView(currentViewIndex);
    }

    /**
     * Hides all views in the stack and shows only the one at the specified index.
     * Also updates the label to indicate which view is active.
     * @param index
     */
    private void showView(int index) {
        for (int i = 0; i < stackCreate.getChildren().size(); i++)
            stackCreate.getChildren().get(i).setVisible(i == index);
        actualMethodLabel.setText(viewNames[index]);
    }
}