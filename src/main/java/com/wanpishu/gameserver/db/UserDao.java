package com.wanpishu.gameserver.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.wanpishu.gameserver.util.HibernateUtil;

public class UserDao {
	public static void saveOrUpdate(Object obj) {
		Session session = HibernateUtil.getLocalThreadSession();
		Transaction t = null;
		try {			
			t = session.beginTransaction();			
			session.saveOrUpdate(obj);			
			t.commit();			
		} catch (Exception e) {
			if (t != null) t.rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
	public static void saveOrUpdate(List<Object> objs) {
		if (objs.isEmpty())
			return;
			
		Session session = HibernateUtil.getLocalThreadSession();
		Transaction t = null;		
		try {			
			t = session.beginTransaction();	
			for (int i = 0; i < objs.size(); i++) {
				Object obj = objs.get(i);
				session.saveOrUpdate(obj);
				
				if (i % 10 == 0) {
					session.flush();
					session.clear();
				}
			}
			t.commit();			
		} catch (Exception e) {
			if (t != null) t.rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession();
		}		
	}
	
	public static User getUser(int userId) {
		Session session = HibernateUtil.getLocalThreadSession();
		Transaction t = null;
		User newUser = null;
		try {			
			t = session.beginTransaction();						
			newUser = session.get(User.class, userId);
			t.commit();			
		} catch (Exception e) {
			if (t != null) t.rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession();
		}
		
		return newUser;		
	}
	
	public static UserPayment getUserPayment(int userId) {
		Session session = HibernateUtil.getLocalThreadSession();
		Transaction t = null;
		UserPayment newUser = null;
		try {			
			t = session.beginTransaction();						
			newUser = session.get(UserPayment.class, userId);
			t.commit();			
		} catch (Exception e) {
			if (t != null) t.rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession();
		}
		
		return newUser;		
	}
	
	public static List<User> getUsers(List<Integer> userIds) throws RuntimeException {
		Session session = HibernateUtil.getLocalThreadSession();
		Transaction t = null;
		List<User> newUsers = new ArrayList<User>();
		if (!userIds.isEmpty()){
			try {	
				t = session.beginTransaction();		
				for (Integer userId : userIds) {
					User newUser = session.get(User.class, userId);				
					if (newUser == null)
						newUser = new User(userId);				
					newUsers.add(newUser);
				}			
				t.commit();			
			} catch (Exception e) {
				if (t != null) t.rollback();
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				HibernateUtil.closeSession();
			}
		}

		return newUsers;		
	}
	
	public static List<UserPayment> getUserPayments(List<Integer> userIds) throws RuntimeException {
		Session session = HibernateUtil.getLocalThreadSession();
		Transaction t = null;
		List<UserPayment> newUsers = new ArrayList<UserPayment>();
		if (!userIds.isEmpty()){
			try {	
				t = session.beginTransaction();		
				for (Integer userId : userIds) {
					UserPayment newUser = session.get(UserPayment.class, userId);				
					if (newUser == null)
						newUser = new UserPayment(userId);				
					newUsers.add(newUser);
				}			
				t.commit();			
			} catch (Exception e) {
				if (t != null) t.rollback();
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				HibernateUtil.closeSession();
			}
		}

		return newUsers;		
	}
}
