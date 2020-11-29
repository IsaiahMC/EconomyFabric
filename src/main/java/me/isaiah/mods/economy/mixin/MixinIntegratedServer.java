package me.isaiah.mods.economy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.isaiah.mods.economy.FabricEconomyMod;
import net.minecraft.server.integrated.IntegratedServer;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {

    @Inject(at = @At(value = "HEAD"), method = "setupServer()Z")
    private void initVar(CallbackInfoReturnable<Boolean> callbackInfo) {
        FabricEconomyMod.MINECRAFT_SERVER = (IntegratedServer) (Object) this;
    }

}