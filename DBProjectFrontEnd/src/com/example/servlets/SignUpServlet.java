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

public class SignUpServlet extends HttpServlet {

	private DBHelper db;
	private static final long serialVersionUID = -6860196849677820488L;
	Logger log = Logger.getLogger(SignUpServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		db = DBHelper.getDBInstance();
		JSONObject out = new JSONObject();
		try {
			JSONObject json = new JSONObject(req.getParameter("json"));
			System.out.println("Received register user request: " + json.toString());
			out.put("status", "failure");
			if(json.getString("type").equals("user") && db.insertUserIntoDB(json)){
				out.put("status", "success");
			} else if (json.getString("type").equals("artist") && db.insertArtistIntoDB(json)){
				out.put("status", "success");
			}
			PrintWriter print = resp.getWriter();
			print.write(out.toString());
			print.flush();
			print.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
