package com.wanpishu.gameserver;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.wanpishu.gameserver.cache.executor.OrderedQueuePoolExecutor;
import com.wanpishu.gameserver.config.ServerConfig;
import com.wanpishu.gameserver.net.Header;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.Helper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 协议处理器
 * 1.创建sessionID
 * 2.接受客户端的消息进行转发
 * @author zhaohui
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<Message> {

	private final static Logger logger = Logger.getLogger(ServerHandler.class);
	
	public final static int COMMAND_OUT_OF_SYNC = -1;
	
	public final static int COMMAND_HEARTBEAT = 0;
	public final static int COMMAND_MAC = 1;
	public final static int COMMAND_DESKID = 2;
	public final static int COMMAND_LOGIN = 3;
	public final static int COMMAND_LOGOUT = 4;
	public final static int COMMAND_USEPROP = 5;
	public final static int COMMAND_USEBLOCK = 6;
	public final static int COMMAND_BUYPRODUCT = 7;
	public final static int COMMAND_CHARGE = 8;
	public final static int COMMAND_PLAY_SINGLE = 9;
	public final static int COMMAND_WIN_SINGLE = 10;
	public final static int COMMAND_CALC_DOUBLE = 11;
	public final static int COMMAND_BELL_RING = 12;
	public final static int COMMAND_BELL_SHUTDOWN = 13;
	public final static int COMMAND_USEGOLD = 14;
	
	public final static int COMMAND_SESSION = 0xff;

	private static OrderedQueuePoolExecutor recvExcutor = new OrderedQueuePoolExecutor(
			"消息接收队列", 100, 10000);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Helper.log("连接已开启" + ctx.channel());
		
		String session = UUID.randomUUID().toString().replace("-", "");
		ServerMain.addChannel(session, ctx.channel());

		JSONObject json = new JSONObject();
		json.put("center_server", ServerConfig.getHost());
		json.put("app_id", ServerConfig.getAppKey());		
		ByteBuf buf = Unpooled.wrappedBuffer(json.toString().getBytes());	
		
		Header header = new Header(session);
		header.setCommandId(COMMAND_SESSION);
		header.setLength(buf.readableBytes());

		ctx.channel().writeAndFlush(new Message(header, buf));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub		
		Helper.log("连接已断开" + ctx.channel());
		
		String session = ServerMain.ChannelMap.inverse().get(ctx.channel());		
		ServerMain.removeChannel(ctx.channel());
		ServerMain.removeDesk(session);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		logger.warn(cause.toString());		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		// TODO Auto-generated method stub			
		String session = ServerMain.ChannelMap.inverse().get(ctx.channel());
		if (session == null || !session.contentEquals(msg.getHeader().getSessionid()))
			return;
			
		if (msg.getHeader().getCommandId() > 0) {
			ByteBuf data = (ByteBuf)msg.getData();
			Helper.log(new String(data.array()));	
				
			addTask(ctx.channel(), msg);
		}
		else {
			Helper.log("heart beat");			
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// TODO Auto-generated method stub
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent)evt;
			if (event.state().equals(IdleState.READER_IDLE)){
				ctx.close();		
			}
		}
	}
	
	public static void addTask(Channel channel, Message msg) {
		synchronized(ServerHandler.class){
			recvExcutor.addTask(msg.getHeader().getSessionid(), new MWork(msg, channel));
		}
	}
}
