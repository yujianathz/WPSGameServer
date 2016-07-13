package com.wanpishu.gameserver;

import org.apache.log4j.Logger;

import com.wanpishu.gameserver.cache.AbstractWork;
import com.wanpishu.gameserver.net.Header;
import com.wanpishu.gameserver.net.Message;
import com.wanpishu.gameserver.util.ErrorCode;
import com.wanpishu.gameserver.util.GsException;
import com.wanpishu.gameserver.util.SpringContainer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

class MWork extends AbstractWork {
	private final static Logger logger = Logger.getLogger(MWork.class);
	
	/** 消息 **/
	private Message request;
	/** 消息队列 **/
	private Channel channel;

	public MWork(Message request, Channel channel) {
		this.request = request;
		this.channel = channel;
	}

	public void run() {
		// TODO Auto-generated method stub
		Message msg = processRequest(request);
		if (msg != null){
			if (msg.getHeader().getCommandId() == ServerHandler.COMMAND_OUT_OF_SYNC){
				ByteBuf data = (ByteBuf) msg.getData();		
				String str = new String(data.array());	
				logger.warn(str);
			}
			channel.writeAndFlush(msg);
		}				
	}
	
	/**
	 * 处理请求
	 * 
	 * @param request
	 *            请求消息
	 * @return
	 */
	private Message processRequest(Message request) {
		int cmdId = getCommandId(request);
		Message response = new Message(getResponseHeader(request, cmdId));
		try {
			AbstractGameHandler handler = (AbstractGameHandler) SpringContainer
					.getInstance().getBeanById("handler" + cmdId);
			if (handler == null) {
				setErrorMsg(-1, ErrorCode.PACKAGE_TAG_ERROR, response);
				return response;
			}
			if (!handler.execute(request, response))
				return null;
		} catch (GsException e) {
			setErrorMsg(-1, e.getErrorCode(), response);
		} catch (Exception ex) {
			setErrorMsg(-1, ErrorCode.SERVER_ERROR, response);
			logger.error("processRequest异常", ex);
		}
		return response;
	}
	
	/**
	 * 获取请求头
	 * 
	 * @param request
	 *            请求
	 * @param cmdId
	 *            协议号
	 * @return
	 */
	private Header getResponseHeader(Message request, int cmdId) {
		Header header = request.getHeader().clone();
		header.setCommandId(cmdId);
		return header;
	}

	/**
	 * 获取协议号
	 * 
	 * @param message
	 *            消息
	 * @return
	 */
	private int getCommandId(Message message) {
		return message.getHeader().getCommandId();
	}

	/**
	 * 设置错误消息体
	 * 
	 * @param state
	 *            响应状态
	 * @param errcode
	 *            错误号
	 * @param response
	 *            返回的消息
	 */
	private void setErrorMsg(int state, ErrorCode errcode, Message response) {
		response.setData(errcode.getName());
	}
}
