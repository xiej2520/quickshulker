package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.minecraft.block.Block;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CloseContainerS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;

public class Util {

    // 1.16 does a mass reworking of Container to ScreenHandler

    public static void openItem(PlayerEntity player, int invSlot) {
        if (invSlot < 0) {
            System.out.println("[QuickShulker]: unknown slot opened");
            //return; //not preventing the crash might make it easier to debug a fix.
        }
        openItem(player, invSlot, ((SlotAccessor) player.container.slots.get(invSlot)).getIndex());
    }

    public static void openItem(PlayerEntity player, int invSlot, int playerInvIndex) {
        if (QuickShulkerMod.getConfig().rightClickClose && playerInvIndex == ((ItemInventoryContainer) player.container).getUsedSlotInPlayerInv()) {
            ((ServerPlayerEntity) player).closeContainer();
            OpenInventoryPacket.send((ServerPlayerEntity) player);
            return;
        }
        ItemStack stack = player.inventory.getInvStack(playerInvIndex);
        stack.removeSubTag(QuickShulkerMod.MOD_ID);
        QuickShulkerData qsData = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsData != null) {
            qsData.openConsumer.accept(player, stack);
            ((ItemInventoryContainer) player.container).setUsedSlot(playerInvIndex);
            player.container.addListener(forceCloseScreenIfNotPresent(player, playerInvIndex, stack));
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        QuickShulkerData qsdata = QuickOpenableRegistry.getQuickie(stack.getItem());
        if (qsdata == null) return false;
        return qsdata.ignoreSingleStackCheck || stack.getCount() <= 1;
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
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areEqualIgnoreDamage(stack1, stack2) && stack1.getCount() == stack2.getCount();
    }

    public static ContainerListener forceCloseScreenIfNotPresent(PlayerEntity player, int slotID, ItemStack stack) {
        return new ContainerListener() {
            @Override
            public void onContainerRegistered(Container handler, DefaultedList<ItemStack> stacks) {
                isValid();
            }

            @Override
            public void onContainerSlotUpdate(Container handler, int slotId, ItemStack stack) {
                isValid();
            }

            @Override
            public void onContainerPropertyUpdate(Container handler, int property, int value) {
                isValid();
            }

            public void isValid() {
                if (!areItemsEqual(stack, player.inventory.getInvStack(slotID))) {
                    ((ServerPlayerEntity) player).closeContainer();
                }
            }
        };
    }
}