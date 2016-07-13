package com.wanpishu.gameserver.handler;

import org.json.JSONObject;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.DeskInfo;
import com.wanpishu.gameserver.OutOfSyncException;
import com.wanpishu.gameserver.ServerHandler;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.db.Prop;
import com.wanpishu.gameserver.db.User;
import com.wanpishu.gameserver.db.UserDao;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handler14 extends AbstractGameHandler {
	
	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();		
		String jsonStr = new String(data.array());		
		
		try {
			JSONObject json = new JSONObject(jsonStr);
			int userId = json.getInt("playerId");
			int deskId = json.getInt("deskId");
			int gold = json.getInt("gold");
			String strFormat = String.format("deskId:%d, userId:%d, gold:%d", userId, deskId, gold);
			
			DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
			if (deskInfo == null || !deskInfo.getSession().contentEquals(request.getHeader().getSessionid()))
				throw new OutOfSyncException(strFormat + "[" + "桌号不正确" + "]");
						
			User user = deskInfo.getUser(userId);
			if (user == null)
				throw new OutOfSyncException(strFormat + "[" + "不存在的用户ID" + "]");
			
			if (!user.changeProp(Prop.PROPS_GOLD, -gold))
				throw new OutOfSyncException(strFormat + "[" + "宝石不足" + "]");
			UserDao.saveOrUpdate(user);
		} 
		catch (OutOfSyncException e) {
			response.getHeader().setCommandId(ServerHandler.COMMAND_OUT_OF_SYNC);
			response.setData(Unpooled.wrappedBuffer(e.getMsg().getBytes()));	
			return true;
		}		
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
