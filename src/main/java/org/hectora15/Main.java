package org.hectora15;


import org.hectora15.util.JDBCInterpreter;

public class Main {
    public static void main(String[] args) {

            String url = "jdbc:mysql://localhost:3306/soccer_db";
        String username = "root";
        String password = "admin";

        JDBCInterpreter interpreter = new JDBCInterpreter(url, username, password);

    }
}