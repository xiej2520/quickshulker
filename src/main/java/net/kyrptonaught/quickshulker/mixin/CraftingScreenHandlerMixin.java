package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.CraftingTableMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingTableMenu.class)
public abstract class CraftingScreenHandlerMixin extends InventoryMenu {

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    public void overrideCanUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (((ItemInventoryContainer) this).hasOpenedItem()) {
            ItemStack stack = player.inventory.getStack(((ItemInventoryContainer) this).getPlayerInvUsedSlot());
            if (stack.getItem() == BlockItem.byBlock(Blocks.CRAFTING_TABLE)) {
                cir.setReturnValue(true);
            }
        }
    }
}
