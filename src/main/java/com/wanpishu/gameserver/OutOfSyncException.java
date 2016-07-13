package com.wanpishu.gameserver;

public class OutOfSyncException extends Exception {

	private static final long serialVersionUID = 4122105446553841650L;
	private String mMsg;
	
	public OutOfSyncException(String msg){
		mMsg = msg;
	}
	
	public String getMsg(){
		return mMsg;
	}
}
