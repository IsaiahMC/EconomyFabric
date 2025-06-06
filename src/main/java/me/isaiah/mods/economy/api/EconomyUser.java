package me.isaiah.mods.economy.api;

import java.math.BigDecimal;

public interface EconomyUser {

    public BigDecimal getMoney();

    public void setMoney(BigDecimal balance);
    
    public void economymod$send_message(String msg);

}