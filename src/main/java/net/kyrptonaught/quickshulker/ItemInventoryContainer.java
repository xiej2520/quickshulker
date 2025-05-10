package net.kyrptonaught.quickshulker;

public interface ItemInventoryContainer {
    int getPlayerInvUsedSlot();

    void setPlayerInvUsedSlot(int playerInvSlotID);

    default boolean hasOpenedItem() {
        return getPlayerInvUsedSlot() >= 0;
    }

}
