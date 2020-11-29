package me.isaiah.mods.economy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.isaiah.mods.economy.FabricEconomyMod;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

@Mixin(MinecraftDedicatedServer.class)
public class MixinDedicatedServer {

    @Inject(at = @At(value = "HEAD"), method = "setupServer()Z")
    private void initVar(CallbackInfoReturnable<Boolean> callbackInfo) {
        FabricEconomyMod.MINECRAFT_SERVER = (MinecraftDedicatedServer) (Object) this;
    }

}