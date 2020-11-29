package me.isaiah.mods.economy.mixin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;

import org.spongepowered.asm.mixin.Mixin;

import me.isaiah.mods.economy.FabricEconomyMod;
import me.isaiah.mods.economy.api.EconomyUser;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerMixin implements EconomyUser {

    private BigDecimal balance;
    private File moneyFile;

    private void moneySetup() {
        balance = new BigDecimal(FabricEconomyMod.DEFAULT_BALANCE);
        moneyFile = new File(FabricEconomyMod.BALANCE_DIR, ((PlayerEntity)(Object)this).getUuidAsString() + ".yml");
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        saveBalanceToFile();
    }

    public void saveBalanceToFile() {
        String yml = "name: " + ((PlayerEntity)(Object)this).getName().asString() + "\n"
                + "balance: " + balance;
        try {
            Files.write(moneyFile.toPath(), yml.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public BigDecimal getMoney() {
        if (null == moneyFile)
            moneySetup();

        return balance;
    }

    @Override
    public void setMoney(BigDecimal balance) {
        if (null == moneyFile)
            moneySetup();
        this.balance = balance;

        saveBalanceToFile();
    }

}