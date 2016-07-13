package com.wanpishu.gameserver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wanpishu.gameserver.client.ServerClient;
import com.wanpishu.gameserver.config.ResourceConfig;
import com.wanpishu.gameserver.config.ServerConfig;
import com.wanpishu.gameserver.net.codec.HeaderDecoder;
import com.wanpishu.gameserver.net.codec.HeaderEncoder;
import com.wanpishu.gameserver.util.HibernateUtil;
import com.wanpishu.gameserver.util.SpringContainer;
import com.wanpishu.gameserver.util.XlsxParser;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 服务器启动程序
 * 
 * @author Administrator
 * 
 */
public class ServerMain {

	private static final Logger logger = Logger.getLogger(ServerMain.class);
	
	public static BiMap<String, Channel> ChannelMap = HashBiMap.create();
	public static BiMap<Integer, DeskInfo> DeskMap = HashBiMap.create();
	
	public static void main(String[] args) throws Exception {	
		XlsxParser.parsePurchaseData();
		ServerConfig.load();
		ResourceConfig.load();
		SpringContainer.getInstance().loadSpring();
		
		HibernateUtil.createSessionFactory();
		
		ServerClient.callWatchLogin();
		ServerClient.callWatchPay();
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);			
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline pipeline = ch.pipeline();
					
					pipeline.addLast("pong", new IdleStateHandler(300, 0, 0, TimeUnit.SECONDS));
					pipeline.addLast("decoder", new HeaderDecoder());				
					pipeline.addLast("encoder", new HeaderEncoder());
					pipeline.addLast("handler", new ServerHandler());
				}
				
			});
			
			ChannelFuture f = bootstrap.bind(ServerConfig.getPort());
			
			logger.info("============Server Startup OK============");
			
			f.channel().closeFuture().sync();
			
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void addChannel(String session, Channel channel) {				
		synchronized(ServerMain.class){
			ServerMain.ChannelMap.put(session, channel);		
		}
	}
	
	public static void removeChannel(Channel channel) {
		synchronized(ServerMain.class){
			BiMap<Channel, String> map = ServerMain.ChannelMap.inverse();
			map.remove(channel);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void addDesk(int deskId, String session) {
		synchronized(ServerMain.class){
			logger.log(Priority.INFO, String.format("DeskID:%d connect", deskId));
			ServerMain.DeskMap.put(deskId, new DeskInfo(session, deskId));
		}		
	}
	
	@SuppressWarnings("deprecation")
	public static void removeDesk(String session) {
		synchronized(ServerMain.class){
			try {
				for (BiMap.Entry<Integer, DeskInfo> entry : ServerMain.DeskMap.entrySet()){
					if (entry.getValue().getSession().contentEquals(session)){
						List<Integer> userIds = entry.getValue().getUserIds();
						for (Integer userId : userIds)						
							ServerClient.callLogout(userId, entry.getValue().getDeskId());
						entry.getValue().clearUsers();
						ServerMain.DeskMap.remove(entry.getKey());
						
						logger.log(Priority.INFO, String.format("DeskID:%d disconnect", entry.getKey()));
						break;
					}
				}							
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}
