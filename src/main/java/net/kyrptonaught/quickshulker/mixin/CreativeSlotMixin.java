package net.kyrptonaught.quickshulker.mixin;

import net.minecraft.client.gui.screen.game.inventory.CreativeInventoryScreen;
import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeInventoryScreen.CreativeInventorySlot.class)
public interface CreativeSlotMixin {

    @Accessor(value = "slot")
    InventorySlot getSlot();
}
