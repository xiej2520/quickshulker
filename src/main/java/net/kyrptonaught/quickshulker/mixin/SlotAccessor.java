package net.kyrptonaught.quickshulker.mixin;

import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventorySlot.class)
public interface SlotAccessor {

    // slot.slot is the index of the slot in the inventory it's in
    @Accessor(value = "slot")
    int getInventoryIndex();
}
