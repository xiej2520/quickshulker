package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.kyrptonaught.quickshulker.network.SetUsedSlotPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class Util {

    // 1.16 does a mass reworking of Container to ScreenHandler

    public static void openItem(Player player, int invSlot) {
        if (invSlot < 0 || invSlot >= player.containerMenu.slots.size()) {
            return;
        }
        Slot slot = player.containerMenu.slots.get(invSlot);
        if (!(slot.container instanceof Inventory)) {
            return;
        }
        openItem(player, invSlot, ((SlotAccessor) slot).getIndex());
    }

    public static void openItem(Player player, int invSlot, int playerInvIndex) {
        ItemStack stack = player.inventory.getItem(playerInvIndex);
        stack.removeTagKey(QuickShulkerMod.MOD_ID);
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null && isOpenableItem(stack)) {
            qsData.openConsumer.accept(player, stack);
            ((ItemInventoryContainer) player.containerMenu).setPlayerInvUsedSlot(playerInvIndex);
            ((ItemInventoryContainer) player.containerMenu).setOpenedStack(stack);
            SetUsedSlotPacket.sendUsedSlotPacket((ServerPlayer) player, playerInvIndex);
            player.containerMenu.addSlotListener(forceCloseScreenIfNotPresent(player, playerInvIndex, stack));
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsdata == null) return false;
        return qsdata.ignoreSingleStackCheck || stack.getCount() <= 1;
    }

    public static Container getQuickItemInventory(Player player, ItemStack stack) {
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            if (qsData.supportsBundleing)
                return qsData.getInventory(player, stack);
        }
        return null;
    }

    public static boolean canOpenInHand(ItemStack stack) {
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            return qsData.canOpenInHand;
        }
        return false;
    }

    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return ItemStack.isSameIgnoreDurability(stack1, stack2) && ItemStack.matches(stack1, stack2) && stack1.getCount() == stack2.getCount();
    }

    public static ContainerListener forceCloseScreenIfNotPresent(Player player, int slotID, ItemStack stack) {
        return new ContainerListener() {
            @Override
            public void refreshContainer(AbstractContainerMenu handler, NonNullList<ItemStack> stacks) {
                isValid();
            }

            @Override
            public void slotChanged(AbstractContainerMenu handler, int slotId, ItemStack stack) {
                isValid();
            }

            @Override
            public void setContainerData(AbstractContainerMenu handler, int property, int value) {
                isValid();
            }

            public void isValid() {
                ItemStack current = player.inventory.getItem(slotID);
                if (current != stack || stack.isEmpty()) {
                    ((ServerPlayer) player).closeContainer();
                }
            }
        };
    }
}