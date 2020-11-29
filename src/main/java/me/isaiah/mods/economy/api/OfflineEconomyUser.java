package me.isaiah.mods.economy.api;

import static me.isaiah.mods.economy.FabricEconomyMod.MINECRAFT_SERVER;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.UUID;

import me.isaiah.mods.economy.FabricEconomyMod;

public class OfflineEconomyUser implements EconomyUser {

    private String uuid;
    private String name;

    public OfflineEconomyUser(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid.toString();
    }

    private BigDecimal balance;
    private File moneyFile;

    private void moneySetup() {
        balance = new BigDecimal(FabricEconomyMod.DEFAULT_BALANCE);
        moneyFile = new File(FabricEconomyMod.BALANCE_DIR, uuid + ".yml");
        moneyFile.getParentFile().mkdirs();
        if (moneyFile.exists()) {
            try {
                for (String s : Files.readAllLines(moneyFile.toPath())) {
                    if (!s.contains(":"))
                        continue;

                    String[] d = s.split(":");
                    String key = d[0];
                    String value = d[1].trim();

                    if (key.equalsIgnoreCase("balance"))
                        balance = new BigDecimal(value);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveBalanceToFile();
    }

    public void saveBalanceToFile() {
        String yml = "name: " + name + "\nbalance: " + getMoney();
        try {
            Files.write(moneyFile.toPath(), yml.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BigDecimal getMoney() {
        if (isOnline())
            return ((EconomyUser) MINECRAFT_SERVER.getPlayerManager().getPlayer(name)).getMoney();

        if (null == moneyFile)
            moneySetup();

        return balance;
    }

    @Override
    public void setMoney(BigDecimal balance) {
        if (isOnline()) {
            ((EconomyUser) MINECRAFT_SERVER.getPlayerManager().getPlayer(name)).setMoney(balance);
            return;
        }

        if (null == moneyFile)
            moneySetup();
        this.balance = balance;

        saveBalanceToFile();
    }

    public boolean isOnline() {
        return null != MINECRAFT_SERVER.getPlayerManager().getPlayer(name);
    }

}