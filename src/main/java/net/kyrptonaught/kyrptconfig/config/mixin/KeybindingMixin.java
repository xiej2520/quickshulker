package net.kyrptonaught.kyrptconfig.config.mixin;

import net.kyrptonaught.kyrptconfig.config.NonConflicting.ModifyableDefaultKey;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeybindingMixin implements ModifyableDefaultKey {

    @Final
    @Mutable
    @Shadow
    private InputUtil.KeyCode defaultKeyCode;

    public void setDefaultKey(InputUtil.KeyCode newKey) {
        this.defaultKeyCode = newKey;
    }
}
