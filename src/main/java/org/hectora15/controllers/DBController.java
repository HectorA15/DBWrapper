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
        if (createViewController != null) {
            createViewController.onConnectionReady(interpreter);
            createViewController.setOnTableCreatedCallback(this::refreshAllViews);
        }
        if (selectViewController != null) selectViewController.onConnectionReady(interpreter, tablePane);
        if (insertViewController != null) insertViewController.onConnectionReady(interpreter);
        if (deleteViewController != null) deleteViewController.onConnectionReady(interpreter);
        if (updateViewController != null) updateViewController.onConnectionReady(interpreter);
    }

    /**
     * Refreshes all views by triggering updates in each associated view controller.
     *
     * This method performs the following actions:
     * - Calls {@code loadAvailableTables()} in the {@code selectViewController} to reload
     *   and update the available tables view, ensuring the display reflects the current
     *   state of the database.
     * - Calls {@code loadTables()} in the {@code insertViewController} to refresh the list
     *   of tables available for insertion operations.
     * - Calls {@code loadTables()} in the {@code deleteViewController} to update the table
     *   selection options for deletion functionality.
     * - Invokes {@code refreshData()} in the {@code updateViewController} to reset and
     *   reinitialize the update view with the latest available data, clearing previous inputs
     *   and repopulating fields as necessary.
     *
     * Only views whose controllers are non-null will be refreshed. This ensures the method
     * does not attempt to interact with uninitialized controllers.
     */
    private void refreshAllViews() {
        if (selectViewController != null) selectViewController.loadAvailableTables();
        if (insertViewController  != null) insertViewController.loadTables();
        if (deleteViewController  != null) deleteViewController.loadTables();
        if (updateViewController  != null) updateViewController.refreshData();
    }

    /**
     * Navigates through a set of views in a circular manner based on the provided direction.
     * Updates the current view index, ensuring the index remains within valid bounds,
     * and switches to the corresponding view.
     *
     * @param direction the step size indicating the navigation direction
     *                  and number of views to move. A positive value moves
     *                  forward, while a negative value moves backward.
     */
    private void navigate(int direction) {
        int size = stackCreate.getChildren().size();
        currentViewIndex = (currentViewIndex + direction + size) % size;
        showView(currentViewIndex);
    }


    /**
     * Updates the visibility of child views within the stack pane and refreshes the associated
     * view controller based on the specified view index. This method sets the specified view
     * as visible while hiding all others, updates the display label with the current view's name,
     * and resets the state of the view by invoking its refresh method.
     *
     * @param index the index of the view to display. It determines which view will be made
     *              visible and triggers a refresh of the associated controller. The value
     *              must correspond to a valid index in the {@code viewNames} array.
     */
    private void showView(int index) {
        for (int i = 0; i < stackCreate.getChildren().size(); i++)
            stackCreate.getChildren().get(i).setVisible(i == index);

        actualMethodLabel.setText(viewNames[index]);

        switch (viewNames[index]) {
            // case "CREATE" -> { if (createViewController  != null) createViewController.refreshData();}
            case "INSERT" -> { if (insertViewController  != null) insertViewController.refreshData(); }
            // case "SELECT" -> { if (selectViewController  != null) selectViewController.refreshData(); }
            case "DELETE" -> { if (deleteViewController  != null) deleteViewController.refreshData(); }
            case "UPDATE" -> { if (updateViewController  != null) updateViewController.refreshData(); }
        }
    }
}