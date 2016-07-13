package com.wanpishu.gameserver.client;

import org.json.JSONObject;

public class HttpResponseBody {
	
	private int responseCode = 0;
	private String responseMsg = "";
	private Object responseResult = null;
	
	HttpResponseBody(String response) {
		JSONObject jsonObject = new JSONObject(response);
		
		responseCode = jsonObject.getInt("code");
		
		if (jsonObject.has("msg"))
			responseMsg = jsonObject.getString("msg");
		if (jsonObject.has("results"))
			responseResult = jsonObject.get("results");			
	}

	public int getResponseCode() {
		return responseCode;
	}
	
	public String getResponseMsg() {
		return responseMsg;
	}
	
	public Object getResponseResult() {
		return responseResult;
	}
}
