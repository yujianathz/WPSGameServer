package com.wanpishu.gameserver.db;

public class UserPayment {
    private Integer id;

    private Integer money;

    public UserPayment() {
    	
    }

    public UserPayment(Integer userId) {
    	this.setId(userId);
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMoney() {
    	return money == null ? 0 : money;
    }
    
    public void setMoney(Integer money) {
    	this.money = money;
    }
}