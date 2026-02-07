package net.kyrptonaught.quickshulker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.api.OpenableItemUtil;
import net.kyrptonaught.quickshulker.mixin.CreativeSlotMixin;
import net.kyrptonaught.quickshulker.mixin.SlotAccessor;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.menu.CreativeInventoryScreen;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.CreativeModeTab;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    public static boolean tryOpenAndSendPacket(ItemStack stack, int playerInvIndex) {
        if (OpenableItemUtil.isOpenableItem(stack)) {
            OpenShulkerPacket.sendOpenPacket(playerInvIndex);
            return true;
        }
        return false;
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
