package me.isaiah.mods.economy;

import static me.isaiah.mods.economy.FabricEconomyMod.MINECRAFT_SERVER;

import net.minecraft.server.network.ServerPlayerEntity;

public class Utils {

	public static ServerPlayerEntity getPlayer(String name) {
        return MINECRAFT_SERVER.getPlayerManager().getPlayer(name);
    }
	
}
