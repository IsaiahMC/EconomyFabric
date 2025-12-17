package me.isaiah.mods.economy;

import static me.isaiah.mods.economy.FabricEconomyMod.MINECRAFT_SERVER;

import net.minecraft.server.level.ServerPlayer;

public class Utils {

	public static ServerPlayer getPlayer(String name) {
        return MINECRAFT_SERVER.getPlayerList().getPlayerByName(name);
    }
	
}
