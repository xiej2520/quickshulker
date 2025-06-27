package net.kyrptonaught.quickshulker.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitComplete(CallbackInfo ci) {
        FabricLoader.getInstance().getEntrypoints(MOD_ID, RegisterQuickShulker.class).forEach(RegisterQuickShulker::registerProviders);
    }

}
