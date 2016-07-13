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
public class ResourceConfig {

	private static Log logger = LogFactory.getLog(ResourceConfig.class);

	private static Properties props = new Properties();

	public static void load() {
		try {
			props.load(new FileReader("conf/resource.properties"));
		} catch (Exception e) {
			logger.error("加载配置文件异常", e);
		}
	}

	public static Integer getGold() {
		return Integer.valueOf(props.getProperty("gold"));
	}
	
	public static Integer getBomb() {
		return Integer.valueOf(props.getProperty("bomb"));
	}
	
	public static Integer getWave() {
		return Integer.valueOf(props.getProperty("wave"));
	}
	
	public static Integer getShield() {
		return Integer.valueOf(props.getProperty("shield"));
	}
	
	public static Integer getStop() {
		return Integer.valueOf(props.getProperty("stop"));
	}
	
	public static Integer getBlock() {
		return Integer.valueOf(props.getProperty("block"));
	}
	
	public static Integer getDefaultChapterLock() {
		return Integer.valueOf(props.getProperty("default_chapter_lock"));
	}
}
