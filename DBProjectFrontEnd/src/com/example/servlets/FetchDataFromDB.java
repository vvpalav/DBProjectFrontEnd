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
			JSONObject input = new JSONObject(req.getParameter("json"));
			String type = input.getString("type");
			System.out.println("Received data fetch request with type: " + type);
			System.out.println(input);
			if(type.equalsIgnoreCase("fetchArtistListForUser")){
				writeOnResponse(resp, db.getArtistListForUser(input.getString("username")));
			} else if(type.equalsIgnoreCase("fetchArtistInfoAndConcertList")){
				String aname = input.getString("aname");
				JSONObject object = db.getArtistInfo(aname);
				object.put("concertList", db.getConcertListForArtist(aname));
				writeOnResponse(resp, object);
			} else if(type.equalsIgnoreCase("fetchGenreAndConcertList")){
				JSONObject object = new JSONObject();
				object.put("concertList", db.getConcertListForGenre(input.getString("genre")));
				writeOnResponse(resp, object);
			} else if (type.equalsIgnoreCase("fetchGenreListForUser")){
				writeOnResponse(resp, db.getGenreListForUser(input.getString("username")));
			} else if (type.equalsIgnoreCase("fetchAllArtistList")){
				writeOnResponse(resp, db.getAllArtistList(input.getString("username")));
			} else if (type.equalsIgnoreCase("fetchAllGenreList")){
				writeOnResponse(resp, db.getAllGenreList(input.getString("username")));
			} else if (type.equalsIgnoreCase("followArtistForUser") 
					&& db.insertArtistIntoUserFollowList(input)){
				JSONObject json = new JSONObject();
				json.put("status", "success");
				writeOnResponse(resp,json);
			} else if (type.equalsIgnoreCase("followGenreForUser")){
				JSONObject json = new JSONObject();
				json.put("status", "failure");
				boolean flag = false;
				String genre = db.getGenreIdByName(input.getString("genre"));
				String user = input.getString("username");
				for(String str : db.getAllSubGenreForGenre(genre)){
					flag = db.insertGenreIntoUserFollowList(user, genre, str);
				}
				if(flag) json.put("status", "success");
				writeOnResponse(resp,json);
			} else if(type.equalsIgnoreCase("updateuserinformation")){
				JSONObject json = new JSONObject();
				json.put("status", "failure");
				if (db.updateUserEntryDB(input)) {
					json.put("status", "success");
				}
				writeOnResponse(resp,json);
			} else if (type.equalsIgnoreCase("checkIfUserIsFollowingArtist")){
				JSONObject newJson = new JSONObject();
				newJson.put("status", "failure");
				if(db.checkIfUserIsFollowingArtist(input)){
					newJson.put("status", "success");
				}
				writeOnResponse(resp,newJson);
			} else if(type.equalsIgnoreCase("checkIfUserIsFollowingGenre")){
				JSONObject newJson = new JSONObject();
				newJson.put("status", "failure");
				if(db.checkIfUserIsFollowingGenre(input)){
					newJson.put("status", "success");
				}
				writeOnResponse(resp,newJson);
			} else if(type.equalsIgnoreCase("fetchRecommendedConcertList")){
				writeOnResponse(resp, db.getRecommendedConcertListForUser(input.getString("username")));
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
