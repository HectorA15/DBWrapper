package org.hectora15;


import org.hectora15.util.JDBCInterpreter;

public class Main {
    public static void main(String[] args) {

            String url = "localhost:3306/test";
        String username = "root";
        String password = "admin";

        JDBCInterpreter interpreter = new JDBCInterpreter(url, username, password);

    }
}