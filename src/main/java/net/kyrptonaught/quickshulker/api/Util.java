package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.block.Block;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
        openItem(player, invSlot, player.container.slots.get(invSlot).getIndex());
    }

    public static void openItem(PlayerEntity player, int invSlot, int playerInvIndex) {
        if (QuickShulkerMod.getConfig().rightClickClose && playerInvIndex == ((ItemInventoryContainer) player.container).getUsedSlotInPlayerInv()) {
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new CloseContainerS2CPacket(player.container.syncId));
            player.container.close(player);
            player.container = player.playerContainer;
            OpenInventoryPacket.send((ServerPlayerEntity) player);
            return;
        }
        ItemStack stack = player.getInventory().getStack(playerInvIndex);
        Block item = Block.getBlockFromItem(stack.getItem());
        stack.removeSubTag(QuickShulkerMod.MOD_ID);
        if (QuickOpenableRegistry.quickies.containsKey(item.getClass())) {
            QuickOpenableRegistry.quickies.get(item.getClass()).openConsumer.accept(player, stack);
            ((ItemInventoryContainer) player.container).setUsedSlot(playerInvIndex);
            player.container.addListener(forceCloseScreenIfNotPresent(player, playerInvIndex, stack));
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem)) return false;
        Block block = ((BlockItem) item).getBlock();
        if (!QuickOpenableRegistry.quickies.containsKey(block.getClass()))
            return false;
        return stack.getCount() <= 1;
    }

    public static Inventory getQuickItemInventory(PlayerEntity player, ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (QuickOpenableRegistry.quickies.containsKey(block.getClass())) {
                QuickShulkerData data = QuickOpenableRegistry.quickies.get(block.getClass());
                if (data.supportsBundleing)
                    return data.getInventory(player, stack);
            }
        }
        return null;
    }

    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2) && stack1.getCount() == stack2.getCount();
    }

    public static ContainerListener forceCloseScreenIfNotPresent(PlayerEntity player, ItemStack stack) {
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
                if (!areItemsEqual(stack, player.getInventory().getStack(slotID))) {
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new CloseContainerS2CPacket(player.container.syncId));
                    player.container.close(player);
                    player.container = player.playerContainer;
                }
            }
        };
    }
}