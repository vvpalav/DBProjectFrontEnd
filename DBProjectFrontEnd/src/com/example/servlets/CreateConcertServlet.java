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

public class CreateConcertServlet extends HttpServlet {

	private static final long serialVersionUID = 7141989813419028425L;
	DBHelper db = DBHelper.getDBInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject out = new JSONObject();
			out.put("status", "failure");
			System.out.println(req.getParameter("json"));
			JSONObject json = new JSONObject(req.getParameter("json"));
			if(db.insertSystemConcertIntoDB(json)){
				out.put("status", "success");
				System.out.println("Concert created successfully");
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
