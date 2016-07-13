package com.wanpishu.gameserver.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.wanpishu.gameserver.config.ServerConfig;

public class Helper {
	public static String getUrlParam(String src, String appSecret) throws NoSuchAlgorithmException {
		Map<String, String> map = new TreeMap<String, String>();
		String[] strs = src.split("&");
		for (String str : strs) {
			String[] childStrs = str.split("=");
			map.put(childStrs[0], str);
		}
		
		Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuilder builder = new StringBuilder();
        while (iter.hasNext()) {
        	if (builder.length() > 0)
        		builder.append("&");
        	
            String key = iter.next();
            builder.append(map.get(key));
        }
        
        String newStr = builder.toString() + appSecret;
        return md5(newStr);
	}
	
	private static String md5(String str) throws NoSuchAlgorithmException {
		String result = null;
		//首先判断是否为空
		if(str.isEmpty()){
			return null;
		}
		try{
			//首先进行实例化和初始化
			MessageDigest md = MessageDigest.getInstance("MD5");
			//得到一个操作系统默认的字节编码格式的字节数组
			byte[] btInput = str.getBytes();
			//对得到的字节数组进行处理
			md.update(btInput);
			//进行哈希计算并返回结果
			byte[] btResult = md.digest();
			//进行哈希计算后得到的数据的长度
			StringBuffer sb = new StringBuffer();
			for(byte b : btResult){
				int bt = b&0xff;
				if(bt<16){
					sb.append(0);
				}
				sb.append(Integer.toHexString(bt));
			}
			result = sb.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return result.toLowerCase();
	}
	
	public static void log(String print) {
		if (ServerConfig.isLogEnabled())
			System.out.println(print);
	}
}
