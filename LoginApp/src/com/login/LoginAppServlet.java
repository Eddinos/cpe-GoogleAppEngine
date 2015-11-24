package com.login;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

@SuppressWarnings("serial")
public class LoginAppServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		
		
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		String name = req.getParameter("name");
		String pwd = req.getParameter("password");
		
		Entity user = new Entity("User");
		user.setProperty("name", name);
		user.setProperty("password", pwd);
		
		datastore.put(user);
		
		try {
			resp.getWriter().println("UserName: " + datastore.get(user.getKey()).getProperty("name"));
			resp.getWriter().println("UserPwd: " + datastore.get(user.getKey()).getProperty("password"));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
