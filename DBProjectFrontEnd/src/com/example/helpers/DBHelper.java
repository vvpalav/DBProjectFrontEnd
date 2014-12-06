package com.example.helpers;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.helpers.json.org.json.JSONArray;
import com.example.helpers.json.org.json.JSONException;
import com.example.helpers.json.org.json.JSONObject;
import com.mysql.jdbc.Connection;

public class DBHelper {
	
	private final String dbURL = "jdbc:mysql://db-project.ctg5kek7aepz.us-east-1.rds.amazonaws.com:3306/ProjectDBSchema";
	private Connection conn;
	private static DBHelper db;
	
	public static void main(String[] args){
		DBHelper db = DBHelper.getDBInstance();
		System.out.println(db.getArtistListForUser("palavvinayak123"));
	}
	
	private DBHelper(){
		try {
			System.out.println("Connecting to database");
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = (Connection) DriverManager.getConnection(dbURL, "admin", "adminpassword");
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
	
	public boolean authenticateArtist(String username, String password){
		String sql = "select count(*) from artist_info where aid = ? and password = ?";
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
			stmt.setString(1, json.getString("uid"));
			stmt.setString(2, json.getString("password"));
			stmt.setString(3, json.getString("firstname"));
			stmt.setString(4, json.getString("lastname"));
			stmt.setDate(5, Date.valueOf(json.getString("dob")));
			stmt.setString(6, json.getString("email"));
			stmt.setString(7, json.getString("city"));
			stmt.setInt(8, 0);
			stmt.setDate(9, new Date(new java.util.Date().getTime()));
			stmt.setDate(10, new Date(new java.util.Date().getTime()));
			return (stmt.executeUpdate() > 0);
		} catch (SQLException | JSONException e) {
			System.out.println("Failed to insert user into DB");
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean insertArtistIntoDB(JSONObject json){
		try {
			int count = getCompanyIdByName(json.getString("company"));
			if(count == -1){
				count = insertCompanyIntoDB(json.getString("company"));
			}
			String sql = "insert into artist_info (aid, password, aname, webpage, com_id) "
					+ " values (?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, json.getString("aid"));
			stmt.setString(2, json.getString("password"));
			stmt.setString(3, json.getString("firstname") + " " + json.getString("lastname"));
			stmt.setString(4, json.getString("webpage"));
			stmt.setInt(5, count);
			return stmt.executeUpdate() > 0;
		} catch (JSONException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int insertCompanyIntoDB(String name){
		String sql = "select count(*) from company_info";
		try {
			ResultSet rs = conn.createStatement().executeQuery(sql);
			rs.next();
			int count = rs.getInt(1);
			sql = "insert into company_info values (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, count+1);
			stmt.setString(2, name);
			stmt.executeUpdate();
			System.out.println("Company inserted successfully: " + name);
			return count+1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getCompanyIdByName(String name){
		String sql = "select com_id from company_info where com_name = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public JSONObject getArtistListForUser(String username){
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		String sql = "select a.aname from artist_info a, user_to_artist_follow u "
				+ " where a.aid = u.following_aid and u.my_uid = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				array.put(rs.getString(1));
			}
			json.put("data", array);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
}

