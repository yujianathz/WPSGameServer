package com.wanpishu.gameserver.util;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.wanpishu.gameserver.config.ServerConfig;

public class HibernateUtil {
	private static SessionFactory _sessionFactory = null;
	private static ThreadLocal<Session> _session = new ThreadLocal<Session>();
	
	public static SessionFactory createSessionFactory(){
		if (_sessionFactory == null) {
			Properties extra = new Properties();
			extra.setProperty("hibernate.connection.url", ServerConfig.getDBUrl());
			extra.setProperty("hibernate.connection.username", ServerConfig.getDBUsername());
			extra.setProperty("hibernate.connection.password", ServerConfig.getDBPassword());
			
			Configuration con = new Configuration().configure();
			con.addProperties(extra);
			
			_sessionFactory = con.buildSessionFactory();			
		}
		
		return _sessionFactory;
	}
	
	public static Session getLocalThreadSession() {
		Session s = _session.get();
		if (s == null) {
			s = _sessionFactory.getCurrentSession();
			_session.set(s);
		}
		
		return s;
	}
	
	public static void closeSession() {
		Session s = _session.get();
		if (s != null) {
			_session.set(null);
		}
	}
}
