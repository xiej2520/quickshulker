package net.kyrptonaught.quickshulker;

import net.minecraft.item.ItemStack;

public interface ItemInventoryContainer {
    int getPlayerInvUsedSlot();

    void setPlayerInvUsedSlot(int playerInvSlotID);

    ItemStack getOpenedStack();

    void setOpenedStack(ItemStack stack);

    default boolean hasOpenedItem() {
        return getPlayerInvUsedSlot() >= 0;
    }

}
