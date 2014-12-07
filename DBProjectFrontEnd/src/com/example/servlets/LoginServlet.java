package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.helpers.DBHelper;
import com.example.helpers.json.org.json.JSONException;
import com.example.helpers.json.org.json.JSONObject;

public class LoginServlet extends HttpServlet {

	private DBHelper db;
	private static final long serialVersionUID = 4619902785789413521L;
	Logger log = Logger.getLogger(LoginServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		try {
			json.put("status", "failure");
			JSONObject input = new JSONObject(req.getParameter("json"));
			String username = input.getString("username");
			String password = input.getString("password");
			System.out.println("Received request to validate user: " + username);
			db = DBHelper.getDBInstance();
			if (input.getString("type").equals("user") && db.authenticateUser(username, password)) {
				json.put("status", "success");
				System.out.println(username + " validated successfully");
			} else if (input.getString("type").equals("artist") && db.authenticateArtist(username, password)) {
				json.put("aname", db.getArtistName(username));
				json.put("status", "success");
				System.out.println(username + " validated successfully");
			}
			json.put("type", input.getString("type"));
			PrintWriter print = resp.getWriter();
			print.write(json.toString());
			print.flush();
			print.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
