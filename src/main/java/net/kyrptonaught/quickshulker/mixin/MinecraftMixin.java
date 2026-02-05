package net.kyrptonaught.quickshulker.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.registerProviders;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitComplete(CallbackInfo ci) {
        registerProviders();
    }

}
