package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.container.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// CraftingScreenHandler in 1.16+?
@Mixin(value = {CraftingTableContainer.class, StonecutterContainer.class})
public abstract class CraftingScreenHandlerMixin extends Container {

    protected CraftingScreenHandlerMixin(@Nullable ContainerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void overrideCanUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (((ItemInventoryContainer) this).hasOpenedItem()) {
            ItemStack stack = player.inventory.getInvStack(((ItemInventoryContainer) this).getPlayerInvUsedSlot());
            if (Util.isOpenableItem(stack))
                cir.setReturnValue(true);
        }
    }
}
