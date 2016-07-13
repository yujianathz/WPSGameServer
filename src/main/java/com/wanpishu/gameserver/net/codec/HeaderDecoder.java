package com.wanpishu.gameserver.net.codec;

import java.util.List;

import com.wanpishu.gameserver.net.Header;
import com.wanpishu.gameserver.net.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

/**
 * 对包的头文件进行解码
 * header协议格式:
 * {
 * 		tag             byte    协议头标志位
 * 		encode  		byte
 * 		encrypt  		byte
 * 		extend1  		byte
 * 		extend2  		byte
 * 		sessionid  		string length[32]
 * 		length  		int
 * 		commandId  		int
 * }
 * 
 * @author zhaohui
 *
 */
public class HeaderDecoder extends ByteToMessageDecoder {

	/**头文件长度**/
	public static final int HEAD_LENGHT = 45;
	/** 包头标志 **/
	public static final byte PACKAGE_TAG = 0x01;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		if (buffer.readableBytes() < HEAD_LENGHT) {
			return;
		}
		buffer.markReaderIndex();
		byte tag = buffer.readByte();
		if (tag != PACKAGE_TAG) {
			throw new CorruptedFrameException("非法协议包");
		}
		byte encode = buffer.readByte();
		byte encrypt = buffer.readByte();
		byte extend1 = buffer.readByte();
		byte extend2 = buffer.readByte();
		byte sessionByte[] = new byte[32];
		buffer.readBytes(sessionByte);
		String sessionid = new String(sessionByte);
		int length = buffer.readInt();
		int commandId = buffer.readInt();

		if (buffer.readableBytes() < length) {
			buffer.resetReaderIndex();
			return;
		}

		Header header = new Header(encode, encrypt, extend1, extend2,
				sessionid, length, commandId);
		Message message = new Message(header, buffer.readBytes(length));
		out.add(message);	
	}

}
