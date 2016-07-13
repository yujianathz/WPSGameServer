package com.wanpishu.gameserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.wanpishu.gameserver.db.User;

public class DeskInfo {
	private static final Logger logger = Logger.getLogger(DeskInfo.class);
	
	private String _session;
	private int _deskId;
	private Map<Integer, User> _users;
	
	public DeskInfo(String session, int deskId) {
		_session = session;
		_deskId = deskId;
		_users = new HashMap<Integer, User>();
	}
	
	public String getSession() {
		return _session;
	}
	
	public int getDeskId() {
		return _deskId;
	}
	
	public User getUser(int userId) {
		return _users.get(userId);
	}
	
	@SuppressWarnings("deprecation")
	public void addUser(User user) {
		synchronized(this){
			_users.put(user.getId(), user);
			
			logger.log(Priority.INFO, String.format("UserId:%d login", user.getId()));
		}
	}
	
	@SuppressWarnings("deprecation")
	public void removeUser(Integer userId) {
		synchronized(this){
			_users.remove(userId);
			
			logger.log(Priority.INFO, String.format("UserId:%d logout", userId));
		}
	}
	
	public List<Integer> getUserIds() {
		List<Integer> userIds = new ArrayList<Integer>();
		for (Integer key : _users.keySet())
			userIds.add(key);
		
		return userIds;
	}
	
	public void clearUsers() {
		synchronized(this){
			_users.clear();
		}
	}
}
