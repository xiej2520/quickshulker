package net.kyrptonaught.quickshulker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.OpenShulkerPacket;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

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
        return player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler;

    }

    public static InputUtil.KeyCode keycode;
    public static int getSlotId(ScreenHandler handler, Slot slot) {
        if (handler instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            if (((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).getSelectedTab() == ItemGroup.INVENTORY.getIndex() && slot instanceof CreativeInventoryScreen.CreativeSlot) {
                return ((CreativeSlotMixin) slot).getSlot().id;
            } else {
                return slot.id - 9;
            }
        }
        return slot.id;
    }

    public static boolean isKeybindPressed() {
        if (keycode == null) {
            keycode = InputUtil.fromName(QuickShulkerMod.getConfig().keybinding);
        }
        if (keycode.getCategory() == InputUtil.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getKeyCode()) == 1;
    public static int getPlayerInvSlot(ScreenHandler handler, Slot slot) {
        if (handler instanceof CreativeInventoryScreen.CreativeScreenHandler) {
            if (((CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen).getSelectedTab() == ItemGroup.INVENTORY.getIndex() && slot instanceof CreativeInventoryScreen.CreativeSlot) {
                return ((CreativeSlotMixin) slot).getSlot().getIndex();
            }
        }
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keycode.getKeyCode()) == 1;
        return slot.getIndex();
    }
}
