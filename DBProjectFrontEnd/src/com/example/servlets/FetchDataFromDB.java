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

	DBHelper db = DBHelper.getDBInstance();
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
			if(json.getString("type").equals("fetchArtistListForUser")){
				writeOnResponse(resp, db.getArtistListForUser(json.getString("username")));
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void writeOnResponse(HttpServletResponse resp, JSONObject json){
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
