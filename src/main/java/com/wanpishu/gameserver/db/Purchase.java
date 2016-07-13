package com.wanpishu.gameserver.db;

public class Purchase {
	private int _type;
	private int _price;
	private int _gold;
	private int _wave;
	private int _shield;
	private int _stop;
	private int _bomb;
	private int _tip;
	
	public final static int PRODUCT_TIP = 1;
	public final static int PRODUCT_WAVE = 2;
	public final static int PRODUCT_WAVE_SUPER = 3;
	public final static int PRODUCT_STOP = 4;
	public final static int PRODUCT_STOP_SUPER = 5;
	public final static int PRODUCT_SHIELD = 6;
	public final static int PRODUCT_SHIELD_SUPER = 7;
	public final static int PRODUCT_BOMB = 8;
	public final static int PRODUCT_BOMB_SUPER = 9;
	public final static int PRODUCT_HAOHUA = 10;
	public final static int PRODUCT_TUHAO = 11;
	public final static int PRODUCT_CAIFU = 12;
	public final static int PRODUCT_HAOHUA_JINBI = 13;
	public final static int PRODUCT_FUHUO = 14;
	public final static int PRODUCT_UNLOCK = 15;
	public final static int PRODUCT_LOTTERY = 16;
	public final static int PRODUCT_WIN = 17;
	public final static int PRODUCT_PREPARE = 18;
	
	public Purchase(int type, int price, int gold, int wave, int shield, int stop, int bomb, int tip){
		_type = type;
		_price = price;
		_gold = gold;
		_wave = wave;
		_shield = shield;
		_stop = stop;
		_bomb = bomb;
		_tip = tip;
	}
	
	public int getType() {
		return _type;
	}
	
	public int getPrice() {
		return _price;
	}
	
	public int getGold() {
		return _gold;
	}
	
	public int getWave() {
		return _wave;
	}
	
	public int getShield() {
		return _shield;
	}
	
	public int getStop() {
		return _stop;
	}
	
	public int getBomb() {
		return _bomb;
	}
	
	public int getTip() {
		return _tip;
	}
}
