package me.isaiah.mods.economy.api;

import java.math.BigDecimal;

public interface EconomyUser {

    public BigDecimal getMoney();

    public void setMoney(BigDecimal balance);
    
    public void economymod$send_message(String msg);

    public default BigDecimal economy$getMoney() {
    	return getMoney();
    }
    
    public default void economy$setMoney(BigDecimal balance) {
    	setMoney(balance);
    }
    
}