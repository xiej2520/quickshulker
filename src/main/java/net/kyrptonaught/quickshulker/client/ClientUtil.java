package net.kyrptonaught.quickshulker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.config.ClientConfigs;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShulkerBoxItem;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    public static boolean tryOpenAndSendPacket(ItemStack stack, int playerInvIndex) {
        if (isOpenableItem(stack)) {
            OpenShulkerPacket.sendOpenPacket(playerInvIndex);
            return true;
        }
        return false;
    }

    public static boolean isOpenableItem(ItemStack stack) {
        if (stack.getItem() instanceof ShulkerBoxItem) {
            return ClientConfigs.Options.QUICK_SHULKER_BOX.getValue();
        }
        if (stack.getItem().equals(BlockItem.byBlock(Blocks.ENDER_CHEST))) {
            return ClientConfigs.Options.QUICK_ENDER_CHEST.getValue();
        }
        if (stack.getItem().equals(BlockItem.byBlock(Blocks.CRAFTING_TABLE))) {
            return ClientConfigs.Options.QUICK_CRAFTING_TABLE.getValue();
        }
        return false;
    }

}
