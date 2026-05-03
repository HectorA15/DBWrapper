package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.hectora15.ui.DBMain;
import org.hectora15.util.JDBCInterpreter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBController {

    // NAVIGATION
    @FXML private Label actualMethodLabel;
    @FXML private Button nextButton;
    @FXML private Button previousButton;

    // GRID
    @FXML private GridPane selectorGrid;


    // STACK PANE
    @FXML private StackPane stackCreate;

    // ============ CREATE VIEW ============
    @FXML private VBox createView;
    @FXML private VBox Create;
    @FXML private TextField createTableField;
    @FXML private TextField createQuantityField;
    @FXML private Button createButton;

    // ============ INSERT VIEW ============
    @FXML private VBox insertView;
    @FXML private ComboBox<String> insertTableCombo;
    @FXML private Button insertButton;

    // ============ SELECT VIEW ============
    @FXML private VBox selectView;
    @FXML private VBox selectVbox;
    @FXML private TitledPane selectTableTitled;
    @FXML private ScrollPane selectTableScroll;
    @FXML private TitledPane selectColumnTitled;
    @FXML private ScrollPane selectColumnScroll;
    @FXML private ScrollPane selectWhereScroll;

    // ============ DELETE VIEW ============
    @FXML private VBox deleteView;
    @FXML private ComboBox<String> deleteTableCombo;
    @FXML private TextArea deleteWhereArea;
    @FXML private Button deleteButton;

    private int currentViewIndex = 0;
    private VBox[] views;
    private String[] viewNames = {"CREATE", "INSERT", "SELECT", "DELETE"};
    private JDBCInterpreter interpreter;
    private Connection connect;

    // Controllers
    private CreateViewController createController;
    private InsertViewController insertController;
    private SelectViewController selectController;
    private DeleteViewController deleteController;

    @FXML
    public void initialize(){
        // all views in an array for easy navigation
        views = new VBox[]{createView, insertView, selectView, deleteView};

        //String url = getDatabase().getUrl();
        //String user = getDatabase().getUsername();
        // password = getDatabase().getPassword();

        //interpreter = new JDBCInterpreter(url,user,password);
        //connect = interpreter.getConnect();


        // Controllers injection
        injectControllers();

        // listeners
        nextButton.setOnAction(e -> showNextView());
        previousButton.setOnAction(e -> showPreviousView());

        // start with the first view
        showView(0);
    }

    private void injectControllers() {
        try {
            createController = new CreateViewController(createView);
            createController.initialize();

            insertController = new InsertViewController(insertView);
            insertController.initialize();

            selectController = new SelectViewController(selectView);
            selectController.initialize();

            deleteController = new DeleteViewController(deleteView);
            deleteController.initialize();

            System.out.println("All controllers injected successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConnectionReady() {
        System.out.println("DBController: Connection is ready, notifying child controllers...");

        selectController.onConnectionReady();
        insertController.onConnectionReady();
        deleteController.onConnectionReady();
        createController.onConnectionReady();
    }


    private void showNextView() {
        currentViewIndex = (currentViewIndex + 1) % views.length;
        showView(currentViewIndex);
    }

    private void showPreviousView() {
        currentViewIndex = (currentViewIndex - 1 + views.length) % views.length;
        showView(currentViewIndex);
    }

    private void showView(int index) {
        for (VBox view : views) {
            view.setVisible(false);
        }

        views[index].setVisible(true);
        actualMethodLabel.setText(viewNames[index]);

        System.out.println("Swapping to: " + viewNames[index]);
    }

    private JDBCInterpreter getDatabase() {
        return DBMain.jdbcInterpreter;
    }


}
