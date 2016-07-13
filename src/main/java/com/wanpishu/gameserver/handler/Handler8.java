package com.wanpishu.gameserver.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.DeskInfo;
import com.wanpishu.gameserver.OutOfSyncException;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.db.User;
import com.wanpishu.gameserver.db.UserDao;
import com.wanpishu.gameserver.db.UserPayment;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handler8 extends AbstractGameHandler {
	
	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();		
		String jsonStr = new String(data.array());		
		
		try {			
			JSONObject json = new JSONObject(jsonStr);
			int deskId = json.getInt("deskId");
			DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
			if (deskInfo == null || !deskInfo.getSession().contentEquals(request.getHeader().getSessionid()))
				throw new OutOfSyncException("[" + "桌号不正确" + "]");
			
			JSONArray arr = json.getJSONArray("orders");
			
			List<Integer> userIds = new ArrayList<Integer>();
			List<Integer> userMoneys = new ArrayList<Integer>();
			List<User> users = new ArrayList<User>();
			for (int i = 0; i < arr.length(); i++){
				JSONObject json1 = arr.getJSONObject(i);
				
				int playerId = json1.getInt("playerId");				
				User user = deskInfo.getUser(playerId);
				if (user != null){
					user.setCoin(json1.getInt("remainCoin"));
					users.add(user);
										
					userIds.add(user.getId());
					userMoneys.add(json1.getInt("coin"));
				}
			}			
			List<Object> objs = new ArrayList<Object>();
			for (User user : users)
				objs.add(user);			
			UserDao.saveOrUpdate(objs);
			
			List<UserPayment> userPayments = UserDao.getUserPayments(userIds);
			for (int i = 0; i < userPayments.size(); i++){
				UserPayment userPayment = userPayments.get(i);
				userPayment.setMoney(userPayment.getMoney() + userMoneys.get(i));
			}
			objs = new ArrayList<Object>();
			for (UserPayment userpayment : userPayments)
				objs.add(userpayment);	
			UserDao.saveOrUpdate(objs);
			
			ByteBuf buf = Unpooled.wrappedBuffer(jsonStr.getBytes());
			
			response.getHeader().setLength(buf.readableBytes());	
			response.setData(buf);

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
