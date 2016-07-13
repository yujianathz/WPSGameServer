package com.wanpishu.gameserver.config;

import java.io.FileReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 全局数据配置类
 * 
 * @author Administrator
 * 
 */
public class ServerConfig {

	private static Log logger = LogFactory.getLog(ServerConfig.class);

	private static Properties props = new Properties();

	public static void load() {
		try {
			props.load(new FileReader("conf/config.properties"));
		}		
		catch (Exception e) {
			logger.error("加载配置文件异常", e);
		}
	}

	/**
	 * 获取服务器端口
	 * 
	 * @return
	 */
	public static int getPort() {
		return Integer.valueOf(props.getProperty("game.server.port"));
	}
	
	public static String getAppKey() {
		return props.getProperty("game.app.key");
	}
	
	public static String getAppSecret() {
		return props.getProperty("game.app.secret");
	}

	public static String getHost() {
		return props.getProperty("remote.server.host");
	}
	
	public static boolean isLogEnabled() {
		return Boolean.valueOf(props.getProperty("logger"));
	}
	
	public static String getDBUrl() {
		return props.getProperty("db.url"); 
	}
	
	public static String getDBUsername() {
		return props.getProperty("db.username");
	}
	
	public static String getDBPassword() {
		return props.getProperty("db.password");
	}
}
