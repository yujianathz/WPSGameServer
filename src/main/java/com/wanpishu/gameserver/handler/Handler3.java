package com.wanpishu.gameserver.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.DeskInfo;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.db.User;
import com.wanpishu.gameserver.db.UserDao;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handler3 extends AbstractGameHandler {

	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();				
		String jsonStr = new String(data.array());
		
		try {			
			JSONObject json = new JSONObject(jsonStr);
			json = saveUsers2DB(json);
			if (json == null)
				return false;
			
			ByteBuf buf = Unpooled.wrappedBuffer(json.toString().getBytes());
			
			response.getHeader().setLength(buf.readableBytes());	
			response.setData(buf);

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private JSONObject saveUsers2DB(JSONObject json) {
		List<Integer> userIds = new ArrayList<Integer>();
		List<JSONObject> userJsons = new ArrayList<JSONObject>();
		JSONArray array = json.getJSONArray("players");
		int deskId = json.getInt("deskId");
		DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject userJson = array.getJSONObject(i).getJSONObject("player");
			Integer userId = Integer.valueOf(userJson.getString("id"));
			
			if (deskInfo.getUser(userId) == null){
				userIds.add(userId);
				userJsons.add(userJson);				
			}			
		}
		
		List<User> users = UserDao.getUsers(userIds);
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			JSONObject userJson = userJsons.get(i);
			
			user.setCoin(Integer.valueOf(userJson.getString("coin")));
			user.setNickname(userJson.getString("nickName"));
			user.setPlace(userJson.getString("country") + "-" + userJson.getString("province") + "-" + userJson.getString("city"));
			user.setSex(Byte.valueOf(userJson.getString("sex")));
			
			userJson.remove("id");
			userJson.remove("coin");
			userJson.put("id", user.getId().intValue());
			userJson.put("coin", user.getCoin().intValue());			
			userJson.put("gold", user.getGold().intValue());
			userJson.put("bomb", user.getBomb().intValue());
			userJson.put("wave", user.getWave().intValue());
			userJson.put("shield", user.getShield().intValue());
			userJson.put("stop", user.getStop().intValue());
			userJson.put("win", user.getWin().intValue());
			userJson.put("lose", user.getLose().intValue());
			userJson.put("buy_tip", user.getBuyTip().byteValue() > 0);
			userJson.put("max_score", user.getMaxScore().intValue());
			
			String blockBubbles = user.getBlockBubbles();
			JSONObject temp = new JSONObject(blockBubbles);
			userJson.put("block", temp);
			
			String chapters = user.getChapters();
			JSONArray arr = new JSONArray(chapters);
			userJson.put("chapters", arr);			
			deskInfo.addUser(user);
		}
		
		if (users.isEmpty())
			return null;
		
		List<Object> objs = new ArrayList<Object>();
		for (User user : users)
			objs.add(user);
		UserDao.saveOrUpdate(objs);
		
		return json;
	}
}
