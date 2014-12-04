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

public class SignUpServlet extends HttpServlet {

	private static final long serialVersionUID = -6860196849677820488L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DBHelper db = DBHelper.getDBInstance();
		JSONObject out = new JSONObject();
		try {
			JSONObject json = new JSONObject(req.getParameter("json"));
			out.put("status", "failure");
			if(db.insertUserIntoDB(json)){
				out.put("status", "success");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			PrintWriter print = resp.getWriter();
			print.write(out.toString());
			print.flush();
			print.close();
		}
	}
}
