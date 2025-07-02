package net.kyrptonaught.quickshulker.mixin;

import net.minecraft.client.gui.screen.inventory.menu.CreativeInventoryScreen;
import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeInventoryScreen.CreativeInventorySlot.class)
public interface CreativeSlotMixin {

    @Accessor(value = "invSlot")
    InventorySlot getSlot();
}
