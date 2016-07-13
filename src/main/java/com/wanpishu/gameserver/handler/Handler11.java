package com.wanpishu.gameserver.handler;

import org.json.JSONObject;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.DeskInfo;
import com.wanpishu.gameserver.OutOfSyncException;
import com.wanpishu.gameserver.ServerHandler;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.db.User;
import com.wanpishu.gameserver.db.UserDao;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handler11 extends AbstractGameHandler {
	
	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();		
		String jsonStr = new String(data.array());		
		
		try {			
			JSONObject json = new JSONObject(jsonStr);
			int winPlayerId = json.getInt("winPlayerId");
			int losePlayerId = json.getInt("losePlayerId");
			int winScore = json.getInt("winScore");
			int loseScore = json.getInt("loseScore");
			int deskId = json.getInt("deskId");
			String strFormat = String.format("winPlayerId:%d, losePlayerId:%d, deskId:%d", winPlayerId, losePlayerId, deskId);
			
			DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
			if (deskInfo == null || !deskInfo.getSession().contentEquals(request.getHeader().getSessionid()))
				throw new OutOfSyncException(strFormat + "[" + "桌号不正确" + "]");
			
			User winUser = deskInfo.getUser(winPlayerId);			
			if (winUser == null)
				throw new OutOfSyncException(strFormat + "[" + "不存在的用户ID" + "]");
			winUser.setWin(winUser.getWin() + 1);
			if (winScore > winUser.getMaxScore())
				winUser.setMaxScore(winScore);
			UserDao.saveOrUpdate(winUser);
			
			User loseUser = deskInfo.getUser(losePlayerId);
			if (loseUser == null)
				throw new OutOfSyncException(strFormat + "[" + "不存在的用户ID" + "]");
			loseUser.setLose(loseUser.getLose() + 1);
			if (loseScore > loseUser.getMaxScore())
				loseUser.setMaxScore(loseScore);	
			UserDao.saveOrUpdate(loseUser);
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
