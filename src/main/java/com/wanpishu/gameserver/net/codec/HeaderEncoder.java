package com.wanpishu.gameserver.net.codec;

import com.wanpishu.gameserver.net.Header;
import com.wanpishu.gameserver.net.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 对包的头文件进行编码
 * @author zhaohui
 *
 */
public class HeaderEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub	
		ByteBuf buffer = (ByteBuf) msg.getData();
		Header header = msg.getHeader();

		out.writeByte(HeaderDecoder.PACKAGE_TAG);
		out.writeByte(header.getEncode());
		out.writeByte(header.getEncrypt());
		out.writeByte(header.getExtend1());
		out.writeByte(header.getExtend2());
		out.writeBytes(header.getSessionid().getBytes());
		if (buffer != null)
			out.writeInt(buffer.readableBytes());
		else
			out.writeInt(0);
		out.writeInt(header.getCommandId());
		if (buffer != null)
			out.writeBytes(buffer);	
	}
}
