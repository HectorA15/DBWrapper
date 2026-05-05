package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.hectora15.util.JDBCInterpreter;

import java.util.ArrayList;
import java.util.List;

public class UpdateViewController {

    @FXML private ComboBox<String> updateTableComboBox;
    @FXML private GridPane updateColumnGrid;
    @FXML private GridPane updateWhereGrid;
    @FXML private Button addAttributeRow;
    @FXML private Button deleteAttributeRow;
    @FXML private Button addWhereRow;
    @FXML private Button deleteWhereRow;
    @FXML private Button updateButton;

    private JDBCInterpreter interpreter;
    private List<String> availableColumns = new ArrayList<>();


    private final List<ComboBox<String>> setCols  = new ArrayList<>();
    private final List<TextField>        setVals  = new ArrayList<>();
    private final List<ComboBox<String>> whereCols = new ArrayList<>();
    private final List<TextField>        whereVals = new ArrayList<>();


    /**
     * Initializes the view.
     * Sets up the event handlers for the buttons and the table selection ComboBox.
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        addAttributeRow .setOnAction(e -> addSetRow());
        deleteAttributeRow.setOnAction(e -> removeLastRow(updateColumnGrid, setCols, setVals));

        addWhereRow .setOnAction(e -> addWhereRow());
        deleteWhereRow.setOnAction(e -> removeLastRow(updateWhereGrid, whereCols, whereVals));
        updateButton.setOnAction(e -> onClickUpdate());
        updateTableComboBox.setOnAction(e -> onTableSelected());
    }

    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
        refreshData();
    }

    /**
     * Refreshes the update view by resetting the available columns, clearing the SET and WHERE grids,
     * and repopulating the table selection ComboBox with the available tables from the database.
     *
     * This method performs the following actions:
     * - Clears the current list of available columns (used for column selection in the SET and WHERE sections).
     * - Updates the contents of the updateTableComboBox with the list of available tables retrieved from the database.
     * - Nullifies the selection in updateTableComboBox to ensure no table is selected by default.
     * - Clears all rows in both the SET and WHERE GridPane sections, including removing associated ComboBox and TextField data.
     * - Adds the initial row to the SET section using {@code addSetRow()}, initializing the grid with one row.
     * - Adds the initial row to the WHERE section using {@code addWhereRow()}, which sets up one condition row.
     */
    public void refreshData() {
        availableColumns.clear();

        updateTableComboBox.getItems().setAll(interpreter.getAvailableTables());
        updateTableComboBox.setValue(null);

        clearGrid(updateColumnGrid, setCols, setVals);
        clearGrid(updateWhereGrid,  whereCols, whereVals);

        addSetRow();
        addWhereRow();
    }


    /**
     * Handles the selection of a table from the ComboBox and updates the available columns
     * for the selected table. Also, refreshes the column ComboBoxes in both the SET and
     * WHERE sections to reflect the columns of the newly selected table.
     *
     * The method performs the following steps:
     * - Retrieves the selected table name from the ComboBox.
     * - Clears the current list of available columns.
     * - If a table is selected, it fetches the column metadata from the database using
     *   {@code interpreter.getTableData()} and populates the list of available columns.
     * - Handles any exceptions that occur during the metadata retrieval process and logs
     *   the error to standard error output.
     * - Updates the column ComboBoxes in the SET and WHERE sections by:
     *   - Replacing the items in the ComboBoxes with the newly fetched list of columns.
     *   - Resetting the selection of each ComboBox to null.
     *
     * This method is triggered automatically when the table selection changes in the UI.
     */
    private void onTableSelected() {
        String table = updateTableComboBox.getValue();
        availableColumns.clear();

        if (table != null) {
            try {
                var meta = interpreter.getTableData(table, "SELECT * FROM " + table + " WHERE 1=0");
                for (int i = 0; i < meta.getColumnCount(); i++)
                    availableColumns.add(meta.getColumnName(i));
            } catch (Exception e) {
                System.err.println("UpdateView: error loading columns: " + e.getMessage());
            }
        }

        setCols .forEach(cb -> { cb.getItems().setAll(availableColumns); cb.setValue(null); });
        whereCols.forEach(cb -> { cb.getItems().setAll(availableColumns); cb.setValue(null); });
    }

    /**
     * Adds a new row to the SET section of the UI, consisting of a ComboBox for column selection
     * and a TextField for entering a corresponding value.
     *
     * This method performs the following steps:
     * - Retrieves the current number of rows in the SET section by checking the size of the {@code setCols} list.
     * - Creates a new ComboBox for column selection using {@code makeColumnCombo()} and a TextField for the value input.
     * - Sets a placeholder prompt text ("new value") for the TextField to indicate its purpose.
     * - Adds the newly created ComboBox and TextField to their respective lists ({@code setCols} and {@code setVals}),
     *   which are used to maintain parallel data structures for the SET section.
     * - Appends the new UI components (ComboBox, Label, TextField) as a row to the {@code updateColumnGrid} GridPane
     *   at the appropriate positions (column 0 for ComboBox, column 1 for Label with "→", and column 2 for TextField).
     */
    private void addSetRow() {
        int row = setCols.size();
        ComboBox<String> cb = makeColumnCombo();
        TextField tf = new TextField();
        tf.setPromptText("new value");

        setCols.add(cb);
        setVals.add(tf);

        updateColumnGrid.add(cb,             0, row);
        updateColumnGrid.add(new Label("→"), 1, row);
        updateColumnGrid.add(tf,             2, row);
    }

    /**
     * Adds a new row to the WHERE section of the UI, consisting of a ComboBox for column selection,
     * a TextField for entering a condition value, and a Label for the "=" operator.
     *
     * The method performs the following steps:
     * - Retrieves the current number of rows in the WHERE section by checking the size of the {@code whereCols} list.
     * - Creates a new ComboBox for column selection using {@code makeColumnCombo()} and a TextField for the condition value.
     * - Sets a placeholder prompt text ("value") for the TextField to indicate its purpose.
     * - Adds the newly created ComboBox and TextField to their respective lists ({@code whereCols} and {@code whereVals})
     *   to maintain parallel data structures for the WHERE section.
     * - Appends the new UI components (ComboBox, Label, TextField) as a row to the {@code updateWhereGrid} GridPane
     *   at the appropriate positions (column 0 for ComboBox, column 1 for Label, and column 2 for TextField).
     *
     * This method is useful for dynamically building the WHERE clause of an SQL UPDATE statement
     * based on user input in the application interface.
     */
    private void addWhereRow() {
        int row = whereCols.size();
        ComboBox<String> cb = makeColumnCombo();
        TextField tf = new TextField();
        tf.setPromptText("value");

        whereCols.add(cb);
        whereVals.add(tf);

        updateWhereGrid.add(cb,             0, row);
        updateWhereGrid.add(new Label("="), 1, row);
        updateWhereGrid.add(tf,             2, row);
    }

    /**
     * Removes the last row from the specified GridPane and the corresponding ComboBox and TextField from the parallel lists.
     * If there are no rows or only one row, the method does nothing to prevent removing all rows.
     * @param grid
     * @param cols
     * @param vals
     */
    private void removeLastRow(GridPane grid,
                               List<ComboBox<String>> cols,
                               List<TextField> vals) {
        if (cols.isEmpty()) return;
        // No permitir bajar de 1 fila
        if (cols.size() <= 1) return;

        int lastRow = cols.size() - 1;

        // Deletes the row from the GridPane by removing all nodes that are in the last row
        grid.getChildren().removeIf(node -> {
            Integer r = GridPane.getRowIndex(node);
            return r != null && r == lastRow;
        });

        cols.remove(lastRow);
        vals.remove(lastRow);
    }

    /**
     * Clears the specified GridPane and the corresponding ComboBox and TextField lists.
     * This is useful when changing the selected table to reset the SET and WHERE sections.
     * @param grid
     * @param cols
     * @param vals
     */
    private void clearGrid(GridPane grid,
                           List<ComboBox<String>> cols,
                           List<TextField> vals) {
        grid.getChildren().clear();
        cols.clear();
        vals.clear();
    }

    /**\
     * Creates a new ComboBox for column selection, populates it with the available columns, and sets a prompt text.
     * This method is used when adding new rows to the SET and WHERE sections to ensure that the ComboBoxes are consistent and up-to-date with the current table's columns.
     * @return
     */
    private ComboBox<String> makeColumnCombo() {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().setAll(availableColumns);
        cb.setPromptText("column");
        return cb;
    }


    /**
     * Handles the click event for the "Update" button, constructing and executing an SQL
     * UPDATE statement based on the user input in the SET and WHERE sections of the UI.
     * Provides feedback to the user regarding the success or failure of the operation.
     *
     * The method performs the following steps:
     *
     * 1. Validates that the interpreter instance and a selected table name are available. If
     *    not, it shows an appropriate warning alert and exits.
     *
     * 2. Constructs the SET clause by iterating over the user-defined column-value pairs in
     *    the SET section. Columns with no selected value or empty values are ignored.
     *
     * 3. Constructs the WHERE clause by iterating over the user-defined condition column-value
     *    pairs in the WHERE section. Empty or incomplete conditions are ignored. A default
     *    "1=1" condition is used if no WHERE clause is specified.
     *
     * 4. Aggregates all values for the SQL query into a single list that combines both the
     *    values from the SET and WHERE sections.
     *
     * 5. Executes the constructed SQL UPDATE statement using the {@code interpreter.update()}
     *    method, passing the table name, SET clause, WHERE clause, and the list of parameter
     *    values.
     *
     * 6. Displays an information alert if the operation is successful or an error alert if
     *    the operation fails. Any exceptions during the execution are caught and displayed.
     */
    public void onClickUpdate() {
        if (interpreter == null) return;

        String table = updateTableComboBox.getValue();
        if (table == null) {
            showAlert(Alert.AlertType.WARNING, "No table", "Select a table first.");
            return;
        }

        // build SET clause
        StringBuilder setStr = new StringBuilder();
        for (int i = 0; i < setCols.size(); i++) {
            String col = setCols.get(i).getValue();
            String val = setVals.get(i).getText().trim();
            if (col == null || val.isEmpty()) continue;
            if (setStr.length() > 0) setStr.append(", ");
            setStr.append(col).append(" = ?");
        }

        // build Where clause
        StringBuilder whereStr = new StringBuilder();
        List<String> whereValues = new ArrayList<>();
        for (int i = 0; i < whereCols.size(); i++) {
            String col = whereCols.get(i).getValue();
            String val = whereVals.get(i).getText().trim();
            if (col == null || val.isEmpty()) continue;
            if (whereStr.length() > 0) whereStr.append(" AND ");
            whereStr.append(col).append(" = ?");
            whereValues.add(val);
        }

        if (setStr.length() == 0) {
            showAlert(Alert.AlertType.WARNING, "No SET values", "Fill at least one attribute to update.");
            return;
        }

        // All values for PreparedStatement: first the SET values, then the WHERE values
        List<Object> allValues = new ArrayList<>();
        for (int i = 0; i < setCols.size(); i++) {
            String col = setCols.get(i).getValue();
            String val = setVals.get(i).getText().trim();
            if (col != null && !val.isEmpty()) allValues.add(val);
        }
        allValues.addAll(whereValues);

        try {
            interpreter.update(table, setStr.toString(),
                    whereStr.length() > 0 ? whereStr.toString() : "1=1",
                    allValues.toArray());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Table '" + table + "' updated.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // Alerts
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}