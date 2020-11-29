package me.isaiah.mods.economy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.isaiah.mods.economy.api.DefaultEconomyProvider;
import me.isaiah.mods.economy.api.IEconomyProvider;
import me.isaiah.mods.economy.api.OfflineEconomyUser;
import me.isaiah.mods.economy.commands.BalCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class FabricEconomyMod implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger("FabricEconomy");
    public static boolean LOADED;
    public static File CONFIG_DIR;
    public static File BALANCE_DIR;
    public static double DEFAULT_BALANCE;

    public static MinecraftServer MINECRAFT_SERVER;

    public static HashMap<String, OfflineEconomyUser> offlineMap;
    public static IEconomyProvider PROVIDER;

    @Override
    public void onInitialize() {
        LOADED = true;
        offlineMap= new HashMap<>();
        CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDirectory(), "FabricEconomy");
        CONFIG_DIR.mkdirs();

        BALANCE_DIR = new File(CONFIG_DIR, "balances");
        BALANCE_DIR.mkdirs();

        File config = new File(CONFIG_DIR, "config.yml");
        if (config.exists()) {
            try {
                for (String s : Files.readAllLines(config.toPath())) {
                    if (!s.contains(":"))
                        continue;

                    String[] d = s.split(":");
                    String key = d[0];
                    String value = d[1].trim();

                    if (key.equalsIgnoreCase("default-balance"))
                        DEFAULT_BALANCE = Double.valueOf(value);

                    if (key.equalsIgnoreCase("provider")) {
                        try {
                            PROVIDER = (IEconomyProvider) Class.forName(value).newInstance();
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null == PROVIDER)
            PROVIDER = new DefaultEconomyProvider();

        String content = "# Config File for FabricEconomy\nconfig-version:1\ndefault-balance: " + DEFAULT_BALANCE + "\n" +
                "provider: " + PROVIDER.getClass().getName();
        try {
            Files.write(config.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Support for offline players
        for (File f : BALANCE_DIR.listFiles()) {
            String name = "";
            String uuid = f.getName().replace(".yml", "");
            try {
                for (String s : Files.readAllLines(f.toPath())) {
                    if (!s.contains(":"))
                        continue;

                    String[] d = s.split(":");
                    String key = d[0];
                    String value = d[1].trim();

                    if (key.equalsIgnoreCase("name"))
                        name = value;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            offlineMap.put(name, new OfflineEconomyUser(name, UUID.fromString(uuid)));
        }

        BalCommand c1 = new BalCommand();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            c1.register(dispatcher, "bal");
        });

        LOGGER.info("FabricEconomy enabled!");
    }

}