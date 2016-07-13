package com.wanpishu.gameserver;

import org.apache.http.HttpResponse;

import org.apache.http.concurrent.FutureCallback;

public class ClientHandler implements FutureCallback<HttpResponse> {

	public void cancelled() {
		// TODO Auto-generated method stub
		
	}

	public void completed(HttpResponse arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0.toString());
	}

	public void failed(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

}
