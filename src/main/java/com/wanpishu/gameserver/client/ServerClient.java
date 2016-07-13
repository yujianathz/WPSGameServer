package com.wanpishu.gameserver.client;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.wanpishu.gameserver.DeskInfo;
import com.wanpishu.gameserver.ServerHandler;
import com.wanpishu.gameserver.ServerMain;
import com.wanpishu.gameserver.config.ServerConfig;
import com.wanpishu.gameserver.net.Header;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.Helper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class ServerClient {

	public static void callWatchLogin() throws Exception {	
		String url = ServerConfig.getHost() + "/game/user/watchLogin?";
		String param = String.format("appkey=%s", ServerConfig.getAppKey());
		param = String.format("%s&sign=%s", param, Helper.getUrlParam(param, ServerConfig.getAppSecret()));
		
		final URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();
		final HttpMethod method = new GetMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "keep-alive");
	
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){									
					try {							
						client.executeMethod(method);
						if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
							String jsonStr = method.getResponseBodyAsString();													
							HttpResponseBody body = new HttpResponseBody(jsonStr);
							if (body.getResponseCode() == 200) {
								// TODO login sucess
								JSONArray array = (JSONArray)body.getResponseResult();
								for (int i = 0; i < array.length(); i++){
									JSONObject json = array.getJSONObject(i);
									int deskId = json.getInt("deskId");
									DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
									if (deskInfo != null) {
										Channel channel = ServerMain.ChannelMap.get(deskInfo.getSession());
										if (channel != null) {
											ByteBuf buf = Unpooled.wrappedBuffer(json.toString().getBytes());							
											Header header = new Header(deskInfo.getSession());
											header.setCommandId(ServerHandler.COMMAND_LOGIN);
											header.setLength(buf.readableBytes());							
											Message msg = new Message(header, buf);	
											
											ServerHandler.addTask(channel, msg);
										}
									}									
								}							
							}
							else if (body.getResponseCode() == 555) {
								Thread.sleep(10000);
							}
							Helper.log(jsonStr);
						}					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}			
		}.start();
	}
	
	public static void callWatchPay() throws Exception {	
		String url = ServerConfig.getHost() + "/game/watchPay?";
		String param = String.format("appkey=%s", ServerConfig.getAppKey());
		param = String.format("%s&sign=%s", param, Helper.getUrlParam(param, ServerConfig.getAppSecret()));
		
		final URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();		
		final HttpMethod method = new GetMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "keep-alive");
	
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){									
					try {							
						client.executeMethod(method);
						if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
							String jsonStr = method.getResponseBodyAsString();													
							HttpResponseBody body = new HttpResponseBody(jsonStr);
							if (body.getResponseCode() == 200) {
								// TODO pay sucess
								JSONArray array = (JSONArray)body.getResponseResult();
								for (int i = 0; i < array.length(); i++){
									JSONObject json = array.getJSONObject(i);
									int deskId = json.getInt("deskId");
									DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
									if (deskInfo != null) {
										Channel channel = ServerMain.ChannelMap.get(deskInfo.getSession());
										if (channel != null) {
											ByteBuf buf = Unpooled.wrappedBuffer(json.toString().getBytes());							
											Header header = new Header(deskInfo.getSession());
											header.setCommandId(ServerHandler.COMMAND_CHARGE);
											header.setLength(buf.readableBytes());							
											Message msg = new Message(header, buf);	
											
											ServerHandler.addTask(channel, msg);
										}
									}									
								}
							}
							else if (body.getResponseCode() == 555) {
								Thread.sleep(10000);
							}
							Helper.log(jsonStr);
						}					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}			
		}.start();
	}
	
	public static void callGetDeskId(final String macId, final String sessionId) throws Exception {
		String url = ServerConfig.getHost() + "/game/desk/queryId?";
		String param = String.format("mac=%s&appkey=%s", macId, ServerConfig.getAppKey());
		param = String.format("%s&sign=%s", param, Helper.getUrlParam(param, ServerConfig.getAppSecret()));
		
		URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();
		final HttpMethod method = new GetMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "close");
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {						
					client.executeMethod(method);
					if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
						String jsonStr = method.getResponseBodyAsString();							
						HttpResponseBody body = new HttpResponseBody(jsonStr);
						if (body.getResponseCode() == 200) {
							// TODO deskId	
							JSONObject json = (JSONObject)body.getResponseResult();
							
							Channel channel = ServerMain.ChannelMap.get(sessionId);
							if (channel != null) {
								ByteBuf buf = Unpooled.wrappedBuffer(json.toString().getBytes());							
								Header header = new Header(sessionId);
								header.setCommandId(ServerHandler.COMMAND_DESKID);
								header.setLength(buf.readableBytes());							
								Message msg = new Message(header, buf);	
								
								ServerHandler.addTask(channel, msg);	
							}						
						}
						Helper.log(jsonStr);
					}							
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					method.releaseConnection();
				}	
			}			
		}.start();		
	}
	
	public static void callLogout(final int playerId, final int deskId) throws Exception {
		String url = ServerConfig.getHost() + "/game/user/logout?";
		String param = String.format("playerId=%d&deskId=%d&appkey=%s", playerId, deskId, ServerConfig.getAppKey());
		param = String.format("%s&sign=%s", param, Helper.getUrlParam(param, ServerConfig.getAppSecret()));
		
		URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();
		final HttpMethod method = new PostMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "close");
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {						
					client.executeMethod(method);
					if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
						String jsonStr = method.getResponseBodyAsString();							
						HttpResponseBody body = new HttpResponseBody(jsonStr);
						if (body.getResponseCode() == 200) {
							DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
							if (deskInfo != null){
								deskInfo.removeUser(playerId);
							}
						}
						Helper.log(jsonStr);
					}							
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					method.releaseConnection();
				}	
			}			
		}.start();		
	}
	
	public static void callCoinPay(int playerId, final int deskId, int coin, final JSONObject json) throws Exception {
		String url = ServerConfig.getHost() + "/game/coin/pay?";
		String param = String.format("playerId=%d&deskId=%d&coin=%d&caption=bubble_pay&appkey=%s", playerId, deskId, coin, ServerConfig.getAppKey());
		param = String.format("%s&sign=%s", param, Helper.getUrlParam(param, ServerConfig.getAppSecret()));
		
		URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();
		final HttpMethod method = new PostMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "close");
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {						
					client.executeMethod(method);
					if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
						String jsonStr = method.getResponseBodyAsString();							
						HttpResponseBody body = new HttpResponseBody(jsonStr);
						
						DeskInfo deskInfo = ServerMain.DeskMap.get(deskId);
						if (deskInfo != null){
							Channel channel = ServerMain.ChannelMap.get(deskInfo.getSession());
							if (channel != null) {
								json.put("code", body.getResponseCode());
								
								ByteBuf buf = Unpooled.wrappedBuffer(json.toString().getBytes());							
								Header header = new Header(deskInfo.getSession());
								header.setCommandId(ServerHandler.COMMAND_BUYPRODUCT);
								header.setLength(buf.readableBytes());							
								Message msg = new Message(header, buf);	
								
								ServerHandler.addTask(channel, msg);
							}
						}
						Helper.log(jsonStr);
					}							
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					method.releaseConnection();
				}	
			}			
		}.start();				
	}
	
	public static void callBellRing(int deskId) throws Exception {
		String url = ServerConfig.getHost() + "/game/bell/ring?";
		String param = String.format("deskId=%d", deskId);
		
		URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();
		final HttpMethod method = new PostMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "close");
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {						
					client.executeMethod(method);
					if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
						String jsonStr = method.getResponseBodyAsString();							
						Helper.log(jsonStr);
					}							
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					method.releaseConnection();
				}	
			}			
		}.start();				
	}
	
	public static void callBellShutdown(int deskId) throws Exception {
		String url = ServerConfig.getHost() + "/game/bell/shutdown?";
		String param = String.format("deskId=%d", deskId);
		
		URI uri = new URI(url + param);
		
		final HttpClient client = new HttpClient();
		final HttpMethod method = new PostMethod(uri.toASCIIString());
		method.setRequestHeader("Connection", "close");
		
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {						
					client.executeMethod(method);
					if (method.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {							
						String jsonStr = method.getResponseBodyAsString();							
						Helper.log(jsonStr);
					}							
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					method.releaseConnection();
				}	
			}			
		}.start();				
	}
}
