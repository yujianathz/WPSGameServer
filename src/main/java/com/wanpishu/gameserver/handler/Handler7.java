package com.wanpishu.gameserver.handler;

import org.json.JSONObject;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.DeskInfo;
import com.wanpishu.gameserver.OutOfSyncException;
import com.wanpishu.gameserver.ServerHandler;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.client.ServerClient;
import com.wanpishu.gameserver.db.Prop;
import com.wanpishu.gameserver.db.Purchase;
import com.wanpishu.gameserver.db.User;
import com.wanpishu.gameserver.db.UserDao;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;
import com.wanpishu.gameserver.util.XlsxParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handler7 extends AbstractGameHandler {
	
	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();		
		String jsonStr = new String(data.array());		
		
		try {
			JSONObject json = new JSONObject(jsonStr);
			int userId = json.getInt("playerId");
			int deskId = json.getInt("deskId");
			int productId = json.getInt("productId");	
			String metadata = json.getString("metadata"); 	
			
			String strFormat = String.format("deskId:%d, userId:%d, productId:%d, metadata:%s", userId, deskId, productId, metadata);
			
			DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
			if (deskInfo == null || !deskInfo.getSession().contentEquals(request.getHeader().getSessionid()))
				throw new OutOfSyncException(strFormat + "[" + "桌号不正确" + "]");
			
			Purchase p = XlsxParser.PurchaseDatas.get(productId);
			if (p == null)
				throw new OutOfSyncException(strFormat + "[" + "不存在的商品ID" + "]");
			
			User user = deskInfo.getUser(userId);
			if (user == null)
				throw new OutOfSyncException(strFormat + "[" + "不存在的用户ID" + "]");
						
			if (json.has("code")){
				int code = json.getInt("code");
				if (code == 200) {					
					UserDao.saveOrUpdate(user);					
				}
				else
				{
					throw new OutOfSyncException(strFormat + "[" + "与中心服务器数据不同步" + "]");
				}
			}
			else
			{
				if (user.getCoin() < p.getPrice())
					throw new OutOfSyncException(strFormat + "[" + "派币不足" + "]");				
				user.setCoin(user.getCoin() - p.getPrice());
				
				if (productId == Purchase.PRODUCT_PREPARE){
					JSONObject json1 = new JSONObject(metadata);
					int prop1Type = json1.getInt("prop1_type");
					int prop1Num = json1.getInt("prop1_num");
					int prop2Type = json1.getInt("prop2_type");
					int prop2Num = json1.getInt("prop2_num");
					user.changeProp(prop1Type, prop1Num);
					user.changeProp(prop2Type, prop2Num);
				}
				else if (productId == Purchase.PRODUCT_UNLOCK){
					JSONObject json2 = new JSONObject(metadata);
					int chapterId = json2.getInt("chapterId");
		    		if (!user.unlockChapter(chapterId))
		    			throw new OutOfSyncException(strFormat + "[" + "解锁不同步" + "]");	    		
				}
				
				user.changeProp(Prop.PROPS_BOMB, p.getBomb());
				if (productId == Purchase.PRODUCT_CAIFU || productId == Purchase.PRODUCT_HAOHUA_JINBI)
					user.changeProp(Prop.PROPS_GOLD, p.getGold() * 2);
				else
					user.changeProp(Prop.PROPS_GOLD, p.getGold());
				user.changeProp(Prop.PROPS_SHIELD,  p.getShield());
				user.changeProp(Prop.PROPS_STOP, p.getShield());
				user.changeProp(Prop.PROPS_WAVE, p.getWave());
				if (user.getBuyTip() == 0 && p.getTip() > 0)
					user.setBuyTip(Byte.valueOf((byte) 1));
				
				ServerClient.callCoinPay(userId, deskId, p.getPrice(), json);
			}
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
