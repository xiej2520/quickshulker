package net.kyrptonaught.quickshulker.client;

import net.kyrptonaught.quickshulker.mixin.CreativeSlotMixin;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.menu.CreativeInventoryScreen;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.CreativeModeTab;
import net.minecraft.item.ItemStack;

public class ClientUtil {
    public final static int OFF_HAND_SLOT = 45;

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
        return player.menu instanceof CreativeInventoryScreen.CreativePlayerMenu;
    }

    public static int getSlotId(InventoryMenu handler, InventorySlot slot) {
        if (handler instanceof CreativeInventoryScreen.CreativePlayerMenu) {
            if (((CreativeInventoryScreen) Minecraft.getInstance().screen).getSelectedTab() == CreativeModeTab.INVENTORY.getId() && slot instanceof CreativeInventoryScreen.CreativeInventorySlot) {
                return slot.id;
            } else {
                return slot.id - 9;
            }
        }
        return slot.id;
    }

    public static int getPlayerInvSlot(InventoryMenu handler, InventorySlot slot) {
        if (handler instanceof CreativeInventoryScreen.CreativePlayerMenu) {
            if (((CreativeInventoryScreen) Minecraft.getInstance().screen).getSelectedTab() == CreativeModeTab.INVENTORY.getId() && slot instanceof CreativeInventoryScreen.CreativeInventorySlot) {
                return ((SlotAccessor) ((CreativeSlotMixin) slot).getSlot()).getInventoryIndex();
            }
        }
        return ((SlotAccessor) slot).getInventoryIndex();
    }
}
