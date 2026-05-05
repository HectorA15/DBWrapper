package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML private InsertViewController  insertViewController;
    @FXML private CreateViewController  createViewController;
    @FXML private DeleteViewController  deleteViewController;
    @FXML private UpdateViewController  updateViewController;

    private final String[] viewNames = {"CREATE", "INSERT", "SELECT", "DELETE", "UPDATE"};
    private int currentViewIndex = 0;

    @FXML
    public void initialize() {
        nextButton    .setOnAction(e -> navigate(+1));
        previousButton.setOnAction(e -> navigate(-1));
        showView(0);
    }

    public void onConnectionReady(JDBCInterpreter interpreter) {
        if (selectViewController != null)
            selectViewController.onConnectionReady(interpreter, tablePane);
        if (insertViewController != null)
            insertViewController.onConnectionReady(interpreter);
        if (createViewController != null) {
            createViewController.onConnectionReady(interpreter);
            createViewController.setOnTableCreatedCallback(this::refreshAllViews);
        }
        if (deleteViewController != null) {
            deleteViewController.onConnectionReady(interpreter);
            deleteViewController.setOnTableCreatedCallback(this::refreshAllViews);
        }
        if (updateViewController != null)
            updateViewController.onConnectionReady(interpreter);
    }

    private void refreshAllViews() {
        if (selectViewController != null) selectViewController.loadAvailableTables();
        if (insertViewController  != null) insertViewController.loadTables();
        if (deleteViewController  != null) deleteViewController.loadTables();
        if (updateViewController  != null) updateViewController.refreshData();
    }

    private void navigate(int direction) {
        int size = stackCreate.getChildren().size();
        currentViewIndex = (currentViewIndex + direction + size) % size;
        showView(currentViewIndex);
    }

    private void showView(int index) {
        for (int i = 0; i < stackCreate.getChildren().size(); i++)
            stackCreate.getChildren().get(i).setVisible(i == index);

        actualMethodLabel.setText(viewNames[index]);

        // Resetear la vista al entrar a ella
        switch (viewNames[index]) {
            case "INSERT" -> { if (insertViewController  != null) insertViewController.refreshData(); }
            case "DELETE" -> { if (deleteViewController  != null) deleteViewController.refreshData(); }
            case "UPDATE" -> { if (updateViewController  != null) updateViewController.refreshData(); }
        }
    }
}