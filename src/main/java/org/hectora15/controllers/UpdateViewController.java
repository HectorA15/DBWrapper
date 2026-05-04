package org.hectora15.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.hectora15.util.JDBCInterpreter;

public class UpdateViewController {

    @FXML private ComboBox updateTableComboBox; // aqui van a salir todas las tablas existentes actuales
    @FXML private GridPane updateWhereGrid; // TODO: aqui es la tabla donde van a ir aparecniendo los campos donde el usuario va a ir poniendo las condiciones del WHERE, se van a ir añadiendo filas cada vez que el usuario pulse el boton de añadir fila
    @FXML private GridPane updateColumnGrid; // TODO: aqui es la tabla donde van a ir aparecniendo los campos donde el usuario va a ir poniendo las columnas a actualizar, se van a ir añadiendo filas cada vez que el usuario pulse el boton de añadir fila
    @FXML private Button addAttributeRow; // TODO: aqui es el boton que el usuario va a pulsar para añadir una nueva fila a la tabla de columnas a actualizar y agregar los campos anteriores
    @FXML private Button addWhereRow; // TODO: aqui es el boton que el usuario va a pulsar para añadir una nueva fila a la tabla de condiciones del WHERE y agregar los campos anteriores
    @FXML private Button updateButton;
    JDBCInterpreter interpreter;

    @FXML
    public void initialize() {

    }


    public void onClickUpdate(){
        updateButton.setOnAction(e -> {
            //TODO: hacer lo que se tiene que hacer para actualizar la tabla, hay que recoger los datos de las tablas de columnas a actualizar y de condiciones del WHERE, y luego hacer la consulta UPDATE con el JDBCInterpreter
        });
    }

    // NO MOVER OCUPAS ESTE METODO PARA CONSEGUIR EL INTERPRETE, SI LO BORRAS NO TE PUEDES COMUNICAR CON LA BASE DE DATOS NI REALIZAR OPERACIONES
    public void onConnectionReady(JDBCInterpreter interpreter) {
        this.interpreter = interpreter;
    }

}
