package net.kyrptonaught.quickshulker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;

@Mixin(DedicatedServer.class)
@Environment(EnvType.SERVER)
public abstract class DedicatedServerMixin {

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitComplete(CallbackInfoReturnable<Boolean> cir) {
        FabricLoader.getInstance().getEntrypoints(MOD_ID, RegisterQuickShulker.class).forEach(RegisterQuickShulker::registerProviders);
    }

}
