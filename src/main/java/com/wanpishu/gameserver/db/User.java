package com.wanpishu.gameserver.db;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wanpishu.gameserver.config.ResourceConfig;

public class User {
    private Integer id;

    private String nickname;

    private Byte sex;

    private String place;

    private Integer coin;

    private Integer gold;

    private Integer bomb;

    private Integer wave;

    private Integer shield;

    private Integer stop;

    private Integer win;

    private Integer lose;

    private Byte buyTip;

    private Integer maxScore;

    private String blockBubbles;
    
    private String chapters;

    public User() {
    	
    }

    public User(Integer userId) {
    	this.setId(userId);
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
    	return nickname == null ? "" : nickname.trim();
    }

    public void setNickname(String nickname) {
        try {
			this.nickname = nickname == null ? null : new String(nickname.trim().getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Byte getSex() {
        return sex == null ? 0 : sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public String getPlace() {
    	return place == null ? "" : place.trim();
    }

    public void setPlace(String place) {
        try {
			this.place = place == null ? null : new String(place.trim().getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Integer getCoin() {
        return coin == null ? 0 : coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getGold() {
        return gold == null ? ResourceConfig.getGold() : gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getBomb() {
        return bomb == null ? ResourceConfig.getBomb() : bomb;
    }

    public void setBomb(Integer bomb) {
        this.bomb = bomb;
    }

    public Integer getWave() {
        return wave == null ? ResourceConfig.getWave() : wave;
    }

    public void setWave(Integer wave) {
        this.wave = wave;
    }

    public Integer getShield() {
        return shield == null ? ResourceConfig.getShield() : shield;
    }

    public void setShield(Integer shield) {
        this.shield = shield;
    }

    public Integer getStop() {
        return stop == null ? ResourceConfig.getStop() : stop;
    }

    public void setStop(Integer stop) {
        this.stop = stop;
    }

    public Integer getWin() {
        return win == null ? 0 : win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Integer getLose() {
        return lose == null ? 0 : lose;
    }

    public void setLose(Integer lose) {
        this.lose = lose;
    }

    public Byte getBuyTip() {
        return buyTip == null ? 0 : buyTip;
    }

    public void setBuyTip(Byte buyTip) {
        this.buyTip = buyTip;
    }

    public Integer getMaxScore() {
        return maxScore == null ? 0 : maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
    
    public String getBlockBubbles() {
    	if (blockBubbles == null || blockBubbles.isEmpty()) {
    		final int[] BLOCK_BUBBLES = {9, 40, 41, 42, 50, 51}; 		
    		JSONObject json = new JSONObject();
    		
    		for (int bubble : BLOCK_BUBBLES) {
    			json.put("" + bubble, ResourceConfig.getBlock());
    		}
    		
    		blockBubbles = json.toString();
    	}
    	
    	return blockBubbles;
    }
    
    public void setBlockBubbles(String bubbles) {
    	this.blockBubbles = bubbles == null ? null : bubbles.trim();
    }

    public String getChapters() {
    	if (chapters == null || chapters.isEmpty()) {
    		JSONArray arr = new JSONArray();
    		
    		int count = ResourceConfig.getDefaultChapterLock();
    		for (int i = 0; i < count; i++){
    			JSONObject json = new JSONObject();    			
    			json.put("s", 0); // s for score
    			json.put("t", 0);  // t for star
    			json.put("p", false); // p for is_play
    			arr.put(json);
    		}
    		
    		chapters = arr.toString();
    	}    		
    	
        return chapters;
    }

    public void setChapters(String chapters) {
        this.chapters = chapters == null ? null : chapters.trim();
    }
    
    public int getProp(int type) {
    	switch(type) 
    	{
    	case Prop.PROPS_BOMB:
    		return getBomb().intValue();
    	case Prop.PROPS_GOLD:
    		return getGold().intValue();
    	case Prop.PROPS_SHIELD:
    		return getShield().intValue();
    	case Prop.PROPS_STOP:
    		return getStop().intValue();
    	case Prop.PROPS_TIP:
    		return getBuyTip().intValue();
    	case Prop.PROPS_WAVE:
    		return getWave().intValue();
    	}
    	
    	return 0;
    }
    
    public boolean hasProp(int type, int num) {
    	switch(type) 
    	{
    	case Prop.PROPS_BOMB:
    		return getBomb().intValue() >= num;
    	case Prop.PROPS_GOLD:
    		return getGold().intValue() >= num;
    	case Prop.PROPS_SHIELD:
    		return getShield().intValue() >= num;
    	case Prop.PROPS_STOP:
    		return getStop().intValue() >= num;
    	case Prop.PROPS_WAVE:
    		return getWave().intValue() >= num;
    	}
    	
    	return false;
    }
    
    public boolean changeProp(int type, int delta) {
    	if (delta == 0)
    		return false;
    		
    	if (delta < 0 && !this.hasProp(type, -delta))
    		return false;
    	
    	switch(type) 
    	{
    	case Prop.PROPS_BOMB:    	
    		setBomb(getBomb().intValue() + delta);    		
    		break;
    	case Prop.PROPS_GOLD:
    		setGold(getGold().intValue() + delta);    		
    		break;
    	case Prop.PROPS_SHIELD:
    		setShield(getShield().intValue() + delta);    		
    		break;
    	case Prop.PROPS_STOP:
    		setStop(getStop().intValue() + delta);    		
    		break;
    	case Prop.PROPS_WAVE:
    		setWave(getWave().intValue() + delta);    		
    		break;
    	default:
    		return false;
    	}
    	
    	return true;
    }
    
    public boolean isChapterUnlock(int chapterId){
    	JSONArray arr = new JSONArray(getChapters());
    	return chapterId <= arr.length();
    }
    
    public boolean unlockChapter(int chapterId) {				
		JSONArray arr = new JSONArray(getChapters());	    		
		if (chapterId == arr.length() + 1){
			JSONObject json = new JSONObject();			
			json.put("s", 0); // s for score
			json.put("t", 0);  // t for star
			json.put("p", false); // p for is_play
			arr.put(json);
			setChapters(arr.toString());
			
			return true;
		}
		return false;
    }
    
    public boolean playChapter(int chapterId){
    	JSONArray arr = new JSONArray(getChapters());
    	try{
    		JSONObject json = arr.getJSONObject(chapterId - 1);
    		if (json != null ){
        		json.put("p", true);
        		setChapters(arr.toString());        		
    		}    		
    		return true;
    	}catch(JSONException e){
    		e.printStackTrace();
    	}    
    	return false;
    }
    
    public boolean scoreChapter(int chapterId, int score, int star){
    	JSONArray arr = new JSONArray(getChapters());
    	try{
    		JSONObject json = arr.getJSONObject(chapterId - 1);
    		if (json != null ){
        		json.put("s", score);
        		json.put("t", star);
        		setChapters(arr.toString());        		
    		}    		
    		return true;
    	}catch(JSONException e){
    		e.printStackTrace();
    	}    
    	return false;
    }
}