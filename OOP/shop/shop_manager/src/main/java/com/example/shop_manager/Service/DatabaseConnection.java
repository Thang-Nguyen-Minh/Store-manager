package com.example.shop_manager.Service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
	// Database connection details
	private static final String URL = "jdbc:mysql://localhost:3306/shop";
	private static final String USER = "root";
	private static final String PASSWORD = "Thang2k4@13";
	// Private constructor to prevent instantiation
	private DatabaseConnection() {}
	// Method to get the connection
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
