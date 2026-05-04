package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.hectora15.util.JDBCInterpreter;

import java.util.*;

public class CreateViewController {

    private final List<String> DataTypes = Arrays.asList("INT", "VARCHAR(50)", "TEXT", "FLOAT", "DOUBLE", "BOOLEAN");
    @FXML
    private TextField createTableField;
    @FXML
    private TextField createColumnsField; // ej: "id INT PRIMARY KEY, name VARCHAR(100)"
    @FXML
    private Button createButton;
    @FXML
    private TextField createQuantityField;
    @FXML
    private GridPane gridValues;
    private JDBCInterpreter interpreter;
    private Runnable onTableCreatedCallback;

    private final Map<TextField, ComboBox> columnMap = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        gridValues.getChildren().clear();
        createQuantityField.textProperty().addListener((obs, oldVal, newVal) -> generateFields());
        createButton.setOnAction(e -> onCreate());
    }

    /**
     * Called by DBMain when a connection is established and a JDBCInterpreter is ready.
     *
     * @param interpreter
     */
    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
    }


    /**
     * Creates a new table in the database using the JDBCInterpreter.
     * It retrieves the table name and column definitions from the user input, validates them, and then executes the CREATE TABLE statement.
     * If there's an error, it shows an alert with the error message.
     */
    private void onCreate() {
        String tableName = createTableField.getText().trim();

        // Validate that the table name is not empty before proceeding
        if (tableName.isEmpty()) {
            System.err.println("Error: Table name cannot be empty.");
            return;
        }

        StringJoiner columnsDefinition = new StringJoiner(", ");

        for (Map.Entry<TextField, ComboBox> entry : columnMap.entrySet()) {
            String colName = entry.getKey().getText().trim();

            // Extract the value safely to prevent NullPointerException from the editable ComboBox
            Object comboValue = entry.getValue().getValue();
            String dataType = (comboValue != null) ? comboValue.toString().trim() : "";

            // Skip incomplete rows instead of terminating the execution
            if (colName.isEmpty() || dataType.isEmpty()) {
                continue;
            }
            columnsDefinition.add(colName + " " + dataType);
        }

        String finalColumns = columnsDefinition.toString();

        // Ensure at least one valid column was parsed before calling the database interpreter
        if (finalColumns.isEmpty()) {
            System.err.println("Error: No valid columns provided for table creation.");
            return;
        }

        System.out.println("Executing SQL generation for table: " + tableName);

        interpreter.createTable(tableName, finalColumns);



        if (onTableCreatedCallback != null) {
            onTableCreatedCallback.run();
        }

        createTableField.clear();
        createQuantityField.clear();
        gridValues.getChildren().clear();

    }


    private void generateFields() {
        gridValues.getChildren().clear();
        gridValues.setVgap(5);

        columnMap.clear();


        try {
            int quantity = Integer.parseInt(createQuantityField.getText().trim());
            if (quantity <= 0) {
                return;
            }

            for (int i = 0; i < quantity; i++) {
                TextField colName = new TextField();
                ComboBox dataType = new ComboBox();
                dataType.getItems().setAll(DataTypes);
                dataType.getSelectionModel().selectFirst();

                columnMap.put(colName, dataType);

                gridValues.add(colName, 0, i);
                gridValues.add(dataType, 1, i);
            }

        } catch (NumberFormatException e) {
            return;
        }

    }

    public void setOnTableCreatedCallback(Runnable callback) {
        this.onTableCreatedCallback = callback;
    }


}