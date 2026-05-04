package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.hectora15.util.JDBCInterpreter;
import java.util.ArrayList;
import java.util.List;

public class UpdateViewController {

    @FXML private ComboBox<String> updateTableComboBox;
    @FXML private GridPane updateWhereGrid;
    @FXML private GridPane updateColumnGrid;
    @FXML private Button addAttributeRow;
    @FXML private Button addWhereRow;
    @FXML private Button updateButton;
    @FXML private Button deleteAttributeRow;
    @FXML private Button deleteWhereRow;



    private JDBCInterpreter interpreter;

    private List<ComboBox<String>> setCols = new ArrayList<>();
    private List<TextField> setVals = new ArrayList<>();
    private List<ComboBox<String>> whereCols = new ArrayList<>();
    private List<TextField> whereVals = new ArrayList<>();

    @FXML
    public void initialize() {
        addAttributeRow.setOnAction(e -> addRow(updateColumnGrid, setCols, setVals, " -> "));

        addWhereRow.setOnAction(e -> addRow(updateWhereGrid, whereCols, whereVals, " = "));

        updateButton.setOnAction(e -> onClickUpdate());
    }

    private void addRow(GridPane grid, List<ComboBox<String>> colList, List<TextField> valList, String symbol) {
        int row = grid.getRowCount();

        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll("nombre", "dorsal", "id", "posicion");
        cb.setPromptText("Columna");

        TextField tf = new TextField();
        tf.setPromptText("Valor");

        colList.add(cb);
        valList.add(tf);

        grid.add(cb, 0, row);
        grid.add(new Label(symbol), 1, row);
        grid.add(tf, 2, row);
    }

    public void onClickUpdate() {
        if (interpreter == null) return;

        String table = updateTableComboBox.getValue();
        StringBuilder setStr = new StringBuilder();
        for (int i = 0; i < setCols.size(); i++) {
            String c = setCols.get(i).getValue();
            String v = setVals.get(i).getText();
            if (c != null && !v.isEmpty()) {
                if (setStr.length() > 0) setStr.append(", ");
                setStr.append(c).append(" = '").append(v).append("'");
            }
        }

        StringBuilder whereStr = new StringBuilder();
        for (int i = 0; i < whereCols.size(); i++) {
            String c = whereCols.get(i).getValue();
            String v = whereVals.get(i).getText();
            if (c != null && !v.isEmpty()) {
                if (whereStr.length() > 0) whereStr.append(" AND ");
                whereStr.append(c).append(" = '").append(v).append("'");
            }
        }

        try {
            interpreter.update(table, setStr.toString(), whereStr.toString(), new Object[0]);

            Alert a = new Alert(Alert.AlertType.INFORMATION, "¡MySQL actualizó los datos con éxito!");
            a.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }



    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
        updateTableComboBox.getItems().clear();
        updateTableComboBox.getItems().addAll(interpreter.getAvailableTables());
        addRow(updateColumnGrid, setCols, setVals, " -> ");
        addRow(updateWhereGrid, whereCols, whereVals, " = ");
    }
}