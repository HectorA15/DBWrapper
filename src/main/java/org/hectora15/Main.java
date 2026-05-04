package org.hectora15;


import org.hectora15.util.JDBCInterpreter;

public class Main {
    public static void main(String[] args) {

            String url = "jdbc:mysql://localhost:3306/prueba";
        String username = "root";
        String password = "admin";

        JDBCInterpreter interpreter = new JDBCInterpreter(url, username, password);
        interpreter.deleteTable("jugadores");
        interpreter.createTable("jugadores", "nombre_jugador VARCHAR(255), dorsal INT");
        interpreter.insert("jugadores", "nombre_jugador, dorsal", new Object[]{"Maradona", 10});


        interpreter.closeConnection();
    }
}