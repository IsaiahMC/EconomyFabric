package me.isaiah.mods.economy.mixin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import me.isaiah.mods.economy.FabricEconomyMod;
import me.isaiah.mods.economy.api.EconomyUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(PlayerEntity.class)
public class PlayerMixin implements EconomyUser {

	@Unique
    private BigDecimal balance;

	@Unique
    private File moneyFile;

	@Unique
    private void money_setup() {
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
                e.printStackTrace();
            }
        }
        save_balance_to_file();
    }

    public void save_balance_to_file() {
        String yml = "name: " + ((PlayerEntity)(Object)this).getName().getString() + "\n"
                + "balance: " + balance;
        try {
            Files.write(moneyFile.toPath(), yml.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BigDecimal getMoney() {
        if (null == moneyFile) {
            money_setup();
        }
        return balance;
    }

    @Override
    public void setMoney(BigDecimal balance) {
        if (null == moneyFile) {
            money_setup();
        }
        this.balance = balance;
        save_balance_to_file();
    }

	@Override
	public void economymod$send_message(String msg) {
		( (PlayerEntity) (Object) this ).sendMessage(Text.of(FabricEconomyMod.translate_alternate_color_codes('&', msg)), false);
	}


}