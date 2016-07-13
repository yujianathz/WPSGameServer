package com.wanpishu.gameserver.handler;

import com.wanpishu.gameserver.AbstractGameHandler;
import com.wanpishu.gameserver.client.ServerClient;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.GsException;

import io.netty.buffer.ByteBuf;

public class Handler1 extends AbstractGameHandler {

	@Override
	public boolean execute(Message request, Message response) throws GsException {
		ByteBuf data = (ByteBuf) request.getData();		
		String macId = new String(data.array());
		
		try {			
			ServerClient.callGetDeskId(macId, request.getHeader().getSessionid());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
