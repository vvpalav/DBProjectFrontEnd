package com.example.helpers;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.helpers.json.org.json.JSONException;
import com.example.helpers.json.org.json.JSONObject;
import com.mysql.jdbc.Connection;

public class DBHelper {
	
	private final String dbURL = "jdbc:mysql://localhost:3306/ProjectDBSchema";
	private Connection conn;
	private static DBHelper db;
	
	private DBHelper(){
		try {
			System.out.println("Connecting to database");
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = (Connection) DriverManager.getConnection(dbURL, "root", "");
			System.out.println("Connected to database");
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Cannot connect the database!");
			e.printStackTrace();
		}
	}
	
	public static synchronized DBHelper getDBInstance(){
		if(db == null){
			db = new DBHelper();
		}
		return db;
	}
	
	public boolean authenticateUser(String username, String password){
		String sql = "select count(*) from user_info where uid = ? and password = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			if(count > 0)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean insertUserIntoDB(JSONObject json) {
		String sql = "insert into user_info values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, json.getInt("uid"));
			stmt.setString(2, json.getString("password"));
			stmt.setString(3, json.getString("firstName"));
			stmt.setString(4, json.getString("lastName"));
			stmt.setDate(5, Date.valueOf(json.getString("dob")));
			stmt.setString(6, json.getString("email"));
			stmt.setString(7, json.getString("city"));
			stmt.setInt(8, json.getInt("user_repo"));
			stmt.setDate(9, Date.valueOf(json.getString("last_acc_date")));
			stmt.setDate(10, Date.valueOf(json.getString("reg_date")));
		} catch (SQLException | JSONException e) {
			System.out.println("Failed to insert user into DB");
			e.printStackTrace();
		}
		return false;
	}
}

