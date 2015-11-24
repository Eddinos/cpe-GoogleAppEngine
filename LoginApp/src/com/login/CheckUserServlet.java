package com.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

@SuppressWarnings("serial")
public class CheckUserServlet extends HttpServlet {
	public static final String MSG_KEY = "message";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
		props.put(MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT, true);

		Query q = new Query("User");
		q.addFilter("name", Query.FilterOperator.EQUAL,
				req.getParameter("name"));

		PreparedQuery pq = datastore.prepare(q);

		String pwdBase = "";
		for (Entity result : pq.asIterable()) {
			String nameBase = (String) result.getProperty("name");
			pwdBase = (String) result.getProperty("password");
		}

		if ("".equals(pwdBase)) {
			resp.getWriter().println("FAILURE");
		} else if (pwdBase.equals(req.getParameter("password"))) {
			resp.getWriter().println("SUCCESS" + '\n');
			try {
				resp.getWriter().println(
						getWelcomeMsg(datastore, CacheManager.getInstance()
								.getCacheFactory().createCache(props)));
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			resp.getWriter().println("FAILURE2");
		}
	}

	
	private String getWelcomeMsg(DatastoreService datastore, Cache cache) {
		if (cache.get(MSG_KEY) != null) {
			return (String) cache.get(MSG_KEY);
		} else {
			Filter msgWelcomeFilter = new FilterPredicate(
					MSG_KEY,
					FilterOperator.NOT_EQUAL, null);

			Query q = new Query(WelcomeMsgServlet.WCMSG_ENTITY_KEY)
					.setFilter(msgWelcomeFilter);

			PreparedQuery pq = datastore.prepare(q);

			String welcomeString = "";
			for (Entity result : pq.asIterable()) {
				welcomeString = (String) result.getProperty(MSG_KEY);
			}

			System.out.println(welcomeString);
			
			cache.put(MSG_KEY, welcomeString);

			return welcomeString;

		}
	}

}
