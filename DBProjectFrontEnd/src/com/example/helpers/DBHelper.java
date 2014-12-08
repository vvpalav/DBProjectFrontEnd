package com.example.helpers;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.example.helpers.json.org.json.JSONArray;
import com.example.helpers.json.org.json.JSONException;
import com.example.helpers.json.org.json.JSONObject;
import com.mysql.jdbc.Connection;

public class DBHelper {
	
	private final String dbURL = "jdbc:mysql://db-project.ctg5kek7aepz.us-east-1.rds.amazonaws.com:3306/ProjectDBSchema";
	private Connection conn;
	private static DBHelper db;
	
	public static void main(String[] args) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("concertId", "one more concert");
		json.put("cname", "new concert name");
		json.put("aname", "Linkinpark");
		json.put("company", "Universal Studio");
		
		json.put("avail", 100);
		json.put("price", 50);
		json.put("capacity", 100);
		json.put("page", "google.com");
		
		json.put("vname", "Ozone park");
		json.put("street", "77 Street");
		json.put("state", "NY");
		json.put("city", "New York");
		json.put("zip", "11416");
		json.put("country", "USA");
		json.put("genre", "Jazz");
		json.put("date", "2014-01-04 02:07:00");
		
		DBHelper db = DBHelper.getDBInstance();
		System.out.println(db.getConcertListBasedOnArtistsFollowers("palavvinayak123"));
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
		try {
			if(db == null || (db != null && db.conn.isClosed())){
				db = new DBHelper();
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
	
	public JSONObject getGenreListForUser(String username){
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		String sql = "select distinct g_desc from main_genre m, users_music_choice u "
				+ "where m.mgid = u.mgid and u.uid = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
				array.put(rs.getString(1));
			}
			json.put("data", array);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getAllArtistList(String username){
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		String sql = "select distinct aname from artist_info where aid not in (select following_aid from "
				+ " user_to_artist_follow where my_uid = ?)";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
				array.put(rs.getString(1));
			}
			json.put("data", array);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getAllGenreList(String username){
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		String sql = "select distinct g_desc from main_genre where mgid not in "
				+ "(select mgid from users_music_choice where uid = ?)";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
				array.put(rs.getString(1));
			}
			json.put("data", array);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
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
	
	public JSONObject getArtistInfo(String aname){
		JSONObject json = new JSONObject();
		String sql = "select a.aname, webpage, c.com_name from artist_info a, "
				+ "company_info c where a.`com_id` = c.com_id and a.aname = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, aname);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			json.put("aname", rs.getString(1));
			json.put("webpage", rs.getString(2));
			json.put("company", rs.getString(3));
			json.put("skills", getArtistMusicSkills(aname));
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONArray getArtistMusicSkills(String aname){
		JSONArray array = new JSONArray();
		String sql = "select distinct g_desc from artist_music_skills am, main_genre mg, artist_info a "
				+ " where mg.mgid = am.mgid and a.aid = am.aid and a.aname = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, aname);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				array.put(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return array;
	}
	
	public JSONArray getConcertListForGenre(String genre) {
		JSONArray array = new JSONArray();
		String sql = "select s.sys_con_id, s.sys_con_name, s.hyperlink, mg.g_desc, a.aname, "
				+ " s.capacity, s.price, s.avail_tickets, "
				+ " concat(v.vname, v.street, ' ', v.city, ' ', v.state, ' ', v.zip, ' ', v.country) as venue "
				+ " from system_created_concert_info s, venue_info v, artist_info a, main_genre mg "
				+ " where mg.mgid = s.concert_genre and v.vid = s.vid and a.aid = s.artist and mg.g_desc = ?";	
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, genre);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				JSONObject json = new JSONObject();
				json.put("concertId", rs.getString(1));
				json.put("concertName", rs.getString(2));
				json.put("link", rs.getString(3));
				json.put("genre", rs.getString(4));
				json.put("aname", rs.getString(5));
				json.put("capacity", rs.getString(6));
				json.put("price", rs.getString(7));
				json.put("availableTickets", rs.getString(8));
				json.put("venue", rs.getString(9));
				array.put(json);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
	
	public JSONArray getConcertListForArtist(String aname) {
		JSONArray array = new JSONArray();
		String sql = "select s.sys_con_id, s.sys_con_name, s.hyperlink, mg.g_desc, a.aname, "
				+ " s.capacity, s.price, s.avail_tickets, "
				+ " concat(v.vname, v.street, ' ', v.city, ' ', v.state, ' ', v.zip, ' ', v.country) as venue "
				+ " from system_created_concert_info s, venue_info v, artist_info a, main_genre mg "
				+ " where mg.mgid = s.concert_genre and v.vid = s.vid and a.aid = s.artist and a.aname = ?";	
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, aname);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				JSONObject json = new JSONObject();
				json.put("concertId", rs.getString(1));
				json.put("concertName", rs.getString(2));
				json.put("link", rs.getString(3));
				json.put("genre", rs.getString(4));
				json.put("aname", rs.getString(5));
				json.put("capacity", rs.getString(6));
				json.put("price", rs.getString(7));
				json.put("availableTickets", rs.getString(8));
				json.put("venue", rs.getString(9));
				array.put(json);
			}
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
	
	public String getArtistId(String name){
		try {
			ResultSet rs = conn.createStatement()
					.executeQuery("select aid from artist_info where aname = '"+name+"'");
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getArtistName(String aid){
		try {
			ResultSet rs = conn.createStatement()
					.executeQuery("select aname from artist_info where aid = '"+aid+"'");
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getGenreIdByName(String genre){
		try {
			ResultSet rs = conn.createStatement()
					.executeQuery("select mgid from main_genre where g_desc = '"+genre+"'");
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean insertSystemConcertIntoDB(JSONObject json){
		int vid = insertVenueIntoDB(json);
		String sql = "insert into system_created_concert_info values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setObject(1, json.getString("concertId"));
		stmt.setObject(2, json.getString("cname"));
		stmt.setObject(3, vid,  java.sql.Types.VARCHAR);
		stmt.setObject(4, json.getString("date"), java.sql.Types.TIMESTAMP);
		stmt.setString(5, getArtistId(json.getString("aname")));
		stmt.setString(6, getCompanyIdByName(json.getString("company"))+"");
		stmt.setString(7, json.getString("capacity"));
		stmt.setString(8, json.getString("avail"));
		stmt.setString(9, json.getString("price"));
		stmt.setString(10, json.getString("page"));
		stmt.setObject(11, new Date(new java.util.Date().getTime()), java.sql.Types.DATE);
		stmt.setString(12, getGenreIdByName(json.getString("genre")));
		return (stmt.executeUpdate() > 0);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int insertVenueIntoDB(JSONObject json){
		String sql = "select count(*) from venue_info";
		ResultSet rs;
		try {
			rs = conn.createStatement().executeQuery(sql);
			rs.next();
			int count = rs.getInt(1)+1;
			sql = "insert into venue_info values (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setObject(1, count, java.sql.Types.VARCHAR);
			stmt.setObject(2, json.getString("vname"));
			stmt.setString(3, json.getString("street"));
			stmt.setString(4, json.getString("city"));
			stmt.setString(5, json.getString("state"));
			stmt.setString(6, json.getString("country"));
			stmt.setString(7, json.getString("zip"));
			stmt.executeUpdate();
			return count;
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean updateUserEntryDB(JSONObject json) throws JSONException {
		String sql = "update user_info set first_name=?, last_name=?, dob=?, email=?,city=? where uid=?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, json.getString("firstname"));
			stmt.setString(2, json.getString("lastname"));
			stmt.setDate(3, Date.valueOf(json.getString("dob")));
			stmt.setString(4, json.getString("email"));
			stmt.setString(5, json.getString("city"));
			stmt.setString(6,json.getString("username"));
			System.out.println("Prepared statement:"+stmt);
			return (stmt.executeUpdate() > 0);
		} catch (SQLException | JSONException e) {
			System.out.println(sql);
			System.out.println("Prepared Statement failed:"+json.getString("username"));
			System.out.println("Failed to insert user into DB");
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> getAllSubGenreForGenre(String mgid){
		ArrayList<String> list = new ArrayList<String>();
		String sql = "select distinct sgid from sub_genre where mgid = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, mgid);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean insertGenreIntoUserFollowList(String uid, String mgid, String sgid) {
		String sql = "insert into users_music_choice values (?, ?, ?)";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, uid);
			stmt.setString(2, mgid);
			stmt.setString(3, sgid);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean insertArtistIntoUserFollowList(JSONObject json){
		String sql = "insert into user_to_artist_follow values (?, ?, ?)";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, json.getString("username"));
			stmt.setString(2, getArtistId(json.getString("aname")));
			stmt.setDate(3, new Date(new java.util.Date().getTime()));
			return stmt.executeUpdate() > 0;
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean checkIfUserIsFollowingGenre(JSONObject json){
		String sql = "select count(*) from users_music_choice where uid = ? and mgid = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, json.getString("username"));
			stmt.setString(2, getGenreIdByName(json.getString("genre")));
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return (rs.getInt(1) > 0);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean checkIfUserRSVPForConcert(JSONObject json){
		String sql = "select count(*) from user_concert_list where uid = ? and sys_con_id = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, json.getString("username"));
			stmt.setString(2, getGenreIdByName(json.getString("concertId")));
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return (rs.getInt(1) > 0);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean checkIfUserIsFollowingArtist(JSONObject json){
		String sql = "select count(*) from user_to_artist_follow where my_uid = ?"
				+ " and following_aid = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, json.getString("username"));
			stmt.setString(2, getArtistId(json.getString("aname")));
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return (rs.getInt(1) > 0);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public JSONObject getRecommendedConcertListForUser(String username){
		HashSet<String> set = new HashSet<String>();
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			//Concerts based on artist user is following
			JSONArray artistList = getArtistListForUser(username).getJSONArray("data");
			for(int i = 0; i < artistList.length(); i++){
				JSONArray a = getConcertListForArtist(artistList.getString(i));
				for(int j = 0; j < a.length(); j++){
					JSONObject t = a.getJSONObject(j);
					if(!set.contains(t.getString("concertId"))){
						set.add(t.getString("concertId"));
						array.put(t);
					}
				}
			}
			
			//Concerts based on genre user has liked
			JSONArray genreList = getGenreListForUser(username).getJSONArray("data");
			for(int i = 0; i < genreList.length(); i++){
				JSONArray a = getConcertListForGenre(genreList.getString(i));
				for(int j = 0; j < a.length(); j++){
					JSONObject t = a.getJSONObject(j);
					if(!set.contains(t.getString("concertId"))){
						set.add(t.getString("concertId"));
						array.put(t);
					}
				}
			}
			
			//Concerts for which user has RSVP'd
			List<String> list = getConcertIdsForUser(username);
			for(String str : list){
				if(!set.contains(str)){
					set.add(str);
					array.put(getConcertInfoFromConcertId(str));
				}
			}
			
			//Concerts recommended by system based on artist and his followers
			JSONArray conList = getConcertListBasedOnArtistsFollowers(username).getJSONArray("data");
			for(int i = 0; i < conList.length(); i++){
				JSONArray a = getConcertListForGenre(conList.getString(i));
				for(int j = 0; j < a.length(); j++){
					JSONObject t = a.getJSONObject(j);
					if(!set.contains(t.getString("concertId"))){
						set.add(t.getString("concertId"));
						conList.put(t);
					}
				}
			}
			
			json.put("data", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public List<String> getConcertIdsForUser(String user){
		ArrayList<String> array = new ArrayList<String>();
		String sql = "select sys_con_id from user_concert_list where uid = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				array.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return array;
	}
	
	public JSONObject getConcertInfoFromConcertId(String conId){
		JSONObject json = new JSONObject();
		String sql = "select s.sys_con_id, s.sys_con_name, s.hyperlink, mg.g_desc, a.aname, "
				+ " s.capacity, s.price, s.avail_tickets, "
				+ " concat(v.vname, v.street, ' ', v.city, ' ', v.state, ' ', v.zip, ' ', v.country) as venue "
				+ " from system_created_concert_info s, venue_info v, artist_info a, main_genre mg "
				+ " where mg.mgid = s.concert_genre and v.vid = s.vid and a.aid = s.artist and s.sys_con_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, conId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			json.put("concertId", rs.getString(1));
			json.put("concertName", rs.getString(2));
			json.put("link", rs.getString(3));
			json.put("genre", rs.getString(4));
			json.put("aname", rs.getString(5));
			json.put("capacity", rs.getString(6));
			json.put("price", rs.getString(7));
			json.put("availableTickets", rs.getString(8));
			json.put("venue", rs.getString(9));
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getConcertListBasedOnArtistsFollowers(String username){
		JSONObject out = new JSONObject();
		JSONArray array = new JSONArray();
		String sql = "select s.sys_con_id, s.sys_con_name, s.hyperlink, mg.g_desc, a.aname, s.capacity, s.price, s.avail_tickets, " 
				+ " concat(v.vname, v.street, ' ', v.city, ' ', v.state, ' ', v.zip, ' ', v.country) as venue "
				+ " from system_created_concert_info s, venue_info v, artist_info a, main_genre mg where v.vid = s.vid " 
				+ " and a.aid = s.artist and s.concert_genre = mg.mgid and s.sys_con_id in " 
				+ " (select sys_con_id from user_concert_list where uid in (select uid from user_info " 
				+ " where uid != ? and uid in (select my_uid from user_to_artist_follow where following_aid in "
				+ " (select following_aid from user_to_artist_follow where my_uid = ?))))";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, username);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				JSONObject json = new JSONObject();
				json.put("concertId", rs.getString(1));
				json.put("concertName", rs.getString(2));
				json.put("link", rs.getString(3));
				json.put("genre", rs.getString(4));
				json.put("aname", rs.getString(5));
				json.put("capacity", rs.getString(6));
				json.put("price", rs.getString(7));
				json.put("availableTickets", rs.getString(8));
				json.put("venue", rs.getString(9));
				array.put(json);
			}
			out.put("data", array);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return out;
	}
}
