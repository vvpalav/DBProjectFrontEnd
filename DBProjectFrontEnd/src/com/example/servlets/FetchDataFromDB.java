package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.helpers.DBHelper;
import com.example.helpers.json.org.json.JSONException;
import com.example.helpers.json.org.json.JSONObject;

public class FetchDataFromDB extends HttpServlet{

	private DBHelper db = DBHelper.getDBInstance();
	private static final long serialVersionUID = -128525419621759266L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject json = new JSONObject(req.getParameter("json"));
			System.out.println(json);
			String type = json.getString("type");
			System.out.println("Received data fetch request with type: " + type);
			if(type.equalsIgnoreCase("fetchArtistListForUser")){
				writeOnResponse(resp, db.getArtistListForUser(json.getString("username")));
			} else if(type.equalsIgnoreCase("fetchArtistInfoAndConcertList")){
				String aname = json.getString("aname");
				JSONObject object = db.getArtistInfo(aname);
				object.put("concertList", db.getConcertListForArtist(aname));
				writeOnResponse(resp, object);
			} else if(type.equalsIgnoreCase("fetchGenreAndConcertList")){
				JSONObject object = new JSONObject();
				object.put("concertList", db.getConcertListForGenre(json.getString("genre")));
				writeOnResponse(resp, object);
			} else if (type.equalsIgnoreCase("fetchGenreListForUser")){
				writeOnResponse(resp, db.getGenreListForUser(json.getString("username")));
			} else if (type.equalsIgnoreCase("fetchAllArtistList")){
				writeOnResponse(resp, db.getAllArtistList());
			} else if (type.equalsIgnoreCase("fetchAllGenreList")){
				writeOnResponse(resp, db.getAllGenreList());
			} else if (type.equalsIgnoreCase("followArtistForUser") 
					&& db.insertArtistIntoUserFollowList(json)){
				json.put("status", "success");
			} else if (type.equalsIgnoreCase("followGenreForUser")){
				String genre = db.getGenreIdByName(json.getString("genre"));
				String user = json.getString("username");
				for(String str : db.getAllSubGenreForGenre(genre)){
					db.insertGenreIntoUserFollowList(user, genre, str);
				}
				json.put("status", "success");
			} else if(type.equalsIgnoreCase("updateuserinformation")){
				JSONObject newJson = new JSONObject();
				json.put("status", "failure");
				if (db.updateUserEntryDB(json)) {
					newJson.put("status", "success");
					writeOnResponse(resp,newJson);
				} else {
					writeOnResponse(resp,newJson);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void writeOnResponse(HttpServletResponse resp, JSONObject json){
		System.out.println("returned " + json);
		try {
			PrintWriter print = resp.getWriter();
			print.write(json.toString());
			print.flush();
			print.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
