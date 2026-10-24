package net.kyrptonaught.quickshulker.client;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.CreativeSlotMixin;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;

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

    public static boolean isCreativeScreen(Player player) {
        return player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu;

    }

    public static int getSlotId(AbstractContainerMenu handler, Slot slot) {
        if (handler instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            if (((CreativeModeInventoryScreen) Minecraft.getInstance().screen).getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId() && slot instanceof CreativeModeInventoryScreen.SlotWrapper) {
                return ((CreativeSlotMixin) slot).getSlot().index;
            } else {
                return slot.index - 9;
            }
        }
        return slot.index;
    }

    public static int getPlayerInvSlot(AbstractContainerMenu handler, Slot slot) {
        if (handler instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            if (((CreativeModeInventoryScreen) Minecraft.getInstance().screen).getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId() && slot instanceof CreativeModeInventoryScreen.SlotWrapper) {
                // post-1.15: getIndex()
                return ((SlotAccessor) ((CreativeSlotMixin) slot).getSlot()).getIndex();
            }
        }
        // post-1.15: getIndex()
        return ((SlotAccessor) slot).getIndex();
    }
}
