package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.network.SetUsedSlotPacket;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.InventoryMenuListener;
import net.minecraft.item.ItemStack;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;

public class Util {

    //openItem(player, invSlot, ((SlotAccessor) player.menu.slots.get(invSlot)).getInventoryIndex());
    public static void openItem(PlayerEntity player, int playerInvIndex) {
        // need to close current menu and save shulker box data before fetching ItemStack
        // make sure player doesn't reopen shulker boxes and dupe
        ((ServerPlayerEntity) player).closeMenu();
        if (QuickShulkerMod.getConfig().rightClickClose && playerInvIndex == ((ItemInventoryContainer) player.menu).getPlayerInvUsedSlot()) {
            OpenInventoryPacket.send((ServerPlayerEntity) player);
            return;
        }
        ItemStack stack = player.inventory.getStack(playerInvIndex);
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            qsData.openConsumer.accept(player, stack);
            ((ItemInventoryContainer) player.menu).setPlayerInvUsedSlot(playerInvIndex);
            SetUsedSlotPacket.sendUsedSlotPacket((ServerPlayerEntity) player, playerInvIndex);
            player.menu.addListener(forceCloseScreenIfNotPresent(player, playerInvIndex, stack));
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsdata == null) return false;
        return qsdata.ignoreSingleStackCheck || stack.getSize() <= 1;
    }

    public static Inventory getQuickItemInventory(PlayerEntity player, ItemStack stack) {
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
        return ItemStack.matchesItemIgnoreDamage(stack1, stack2) && ItemStack.matchesItem(stack1, stack2) && stack1.getSize() == stack2.getSize();
    }

    public static InventoryMenuListener forceCloseScreenIfNotPresent(PlayerEntity player, int slotID, ItemStack stack) {
        return new InventoryMenuListener() {
            @Override
            public void updateMenu(InventoryMenu menu, DefaultedList<ItemStack> stacks) {
                isValid();
            }

            @Override
            public void onSlotChanged(InventoryMenu menu, int slotId, ItemStack stack) {
                isValid();
            }

            @Override
            public void onDataChanged(InventoryMenu menu, int property, int value) {
                isValid();
            }

            @Override
            public void updateData(InventoryMenu menu, Inventory inventory) {
                // TODO: should this be empty?
            }

            public void isValid() {
                if (!areItemsEqual(stack, player.inventory.getStack(slotID))) {
                    ((ServerPlayerEntity) player).closeMenu();
                }
            }
        };
    }
}