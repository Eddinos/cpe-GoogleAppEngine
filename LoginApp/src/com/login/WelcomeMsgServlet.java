package com.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

@SuppressWarnings("serial")
public class WelcomeMsgServlet extends HttpServlet {
	public static final String WCMSG_ENTITY_KEY = "WelcomeMessage";
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity welcomeMsg = new Entity(WCMSG_ENTITY_KEY);
		welcomeMsg.setProperty("message", "Wesh");
		
		datastore.put(welcomeMsg);
		
		try {
			resp.getWriter().println("Message: " + datastore.get(welcomeMsg.getKey()).getProperty("message"));
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
