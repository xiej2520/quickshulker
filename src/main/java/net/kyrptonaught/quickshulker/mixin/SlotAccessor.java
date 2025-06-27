package net.kyrptonaught.quickshulker.mixin;

import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventorySlot.class)
public interface SlotAccessor {

    @Accessor(value = "slot")
    int getInventoryIndex();
}
