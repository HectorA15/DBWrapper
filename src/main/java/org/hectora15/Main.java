package org.hectora15;


import org.hectora15.util.JDBCInterpreter;

public class Main {
    public static void main(String[] args) {

            String url = "dw";
        String username = "root";
        String password = "admin";

        JDBCInterpreter interpreter = new JDBCInterpreter(url, username, password);

    }
}