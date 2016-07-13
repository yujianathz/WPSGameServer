package com.wanpishu.gameserver.handler;

import org.json.JSONObject;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;

import io.netty.buffer.ByteBuf;

public class Handler2 extends AbstractGameHandler {

	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();	
		String jsonStr = new String(data.array());
		
		try {
			JSONObject json = new JSONObject(jsonStr);
			
			int deskId = json.getInt("deskId");
			ServerMain.addDesk(deskId, request.getHeader().getSessionid());
			
			response.getHeader().setLength(data.readableBytes());
			response.setData(data);
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
