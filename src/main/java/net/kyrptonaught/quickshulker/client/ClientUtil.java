package net.kyrptonaught.quickshulker.client;

import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.Container;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ClientUtil {

    public static boolean CheckAndSend(ItemStack stack, int slot) {
        if (Util.isOpenableItem(stack)) {
            SendOpenPacket(slot);
            return true;
        }
        return false;
    }

    private static void SendOpenPacket(int slot) {
        OpenShulkerPacket.sendOpenPacket(slot);
    }

    public static boolean isCreativeScreen(PlayerEntity player) {
        return player.container instanceof CreativeInventoryScreen.CreativeContainer;

    }

    public static int getSlotId(Container handler, InventorySlot slot) {
        if (handler instanceof CreativeInventoryScreen.CreativeContainer) {
            if (((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).getSelectedTab() == ItemGroup.INVENTORY.getIndex() && slot instanceof CreativeInventoryScreen.CreativeSlot) {
                return ((CreativeSlotMixin) slot).getSlot().id;
            } else {
                return slot.id - 9;
            }
        }
        return slot.id;
    }

    public static int getPlayerInvSlot(Container handler, InventorySlot slot) {
        if (handler instanceof CreativeInventoryScreen.CreativeContainer) {
            if (((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).getSelectedTab() == ItemGroup.INVENTORY.getIndex() && slot instanceof CreativeInventoryScreen.CreativeSlot) {
                // post-1.15: getIndex()
                return ((SlotAccessor) ((CreativeSlotMixin) slot).getSlot()).getInventoryIndex();
            }
        }
        // post-1.15: getIndex()
        return ((SlotAccessor) slot).getInventoryIndex();
    }
}
