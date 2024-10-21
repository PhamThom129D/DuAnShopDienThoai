package com.example.duanshopdienthoai;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL ="jdbc:mysql://localhost:3306/quanlyshopbandienthoai";
    public static Connection getConnection() throws SQLException {
        String USER = System.getenv("DB_USERNAME");
        String PASS = System.getenv("DB_PASSWORD");
        if (USER == null || PASS == null) {
            throw new SQLException("Kết nối Database thất bại");
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

}
