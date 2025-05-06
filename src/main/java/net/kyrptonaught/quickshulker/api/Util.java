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

    public static void openItem(PlayerEntity player, ItemStack stack) {
        Block item = Block.getBlockFromItem(stack.getItem());
        stack.removeSubTag(QuickShulkerMod.MOD_ID);
        if (QuickOpenableRegistry.consumers.containsKey(item.getClass())) {
            QuickOpenableRegistry.consumers.get(item.getClass()).accept(player, stack);
            // 1.16: container -> currentScreenHandler
            ((ItemInventoryContainer) player.container).setOpenedItem(stack);
            player.container.addListener(forceCloseScreenIfNotPresent(player, stack));
        }
    }

    public static void openItem(PlayerEntity player, int invSlot, int type) {
        if (type == 0) {
            if (invSlot == -69) {
                // main hand
                openItem(player, player.getMainHandStack());
            } else if (invSlot >= 0 && invSlot < player.container.slots.size()) {
                // opened container
                openItem(player, player.container.getSlot(invSlot).getStack());
            }
        } else if (type == 1) {
            // 1.16: playerContainer -> playerScreenHandler
            if (invSlot >= 0 && invSlot < player.playerContainer.slots.size())
                // player inventory
                openItem(player, player.playerContainer.getSlot(invSlot).getStack());
        }
    }

    public static Boolean isOpenableItem(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem)) return false;
        Block block = ((BlockItem) item).getBlock();
        if (!(block instanceof EnderChestBlock) && stack.getCount() != 1) return false;
        return QuickOpenableRegistry.consumers.containsKey(block.getClass());
    }

    public static boolean isEnderChest(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem)) return false;
        return ((BlockItem) item).getBlock() instanceof EnderChestBlock;
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
                if (!player.inventory.contains(stack)) {
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new CloseContainerS2CPacket(player.container.syncId));
                    player.container = player.playerContainer;
                }
            }
        };
    }
}