package net.kyrptonaught.quickshulker.client;

import malilib.registry.Registry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.config.ClientConfigs;
import net.kyrptonaught.quickshulker.network.SetUsedSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.lifecycle.api.client.ClientWorldEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;

@Environment(EnvType.CLIENT)
public class QuickShulkerModClient implements ClientModInitializer {

    // need to persist between screens
    public static double LAST_MOUSE_X = -1, LAST_MOUSE_Y = -1;

    public static final int PLAYER_INVENTORY_OFF_HAND_SLOT = 40;

    private static boolean wasKeyHeld;

    @Override
    public void onInitializeClient() {

        ClientWorldEvents.TICK_START.register(clientWorld -> {
            boolean keyHeld = ClientConfigs.HotKeys.QUICKSHULKER_KEYBIND.getKeyBind().isKeyBindHeld();
            boolean keyPressed = keyHeld && !wasKeyHeld;
            wasKeyHeld = keyHeld;
            if (keyPressed && Minecraft.getInstance().screen == null && ClientConfigs.Options.KEYBIND_OPEN_IN_HAND.getValue()) {
                PlayerEntity player = Minecraft.getInstance().player;
                if (player != null) {
                    if (player.getItemInMainHand().isEmpty() && !player.getItemInOffHand().isEmpty()) {
                        ClientUtil.tryOpenAndSendPacket(player.getItemInOffHand(), PLAYER_INVENTORY_OFF_HAND_SLOT);
                    } else {
                        ClientUtil.tryOpenAndSendPacket(player.getItemInMainHand(), player.inventory.selectedSlot);
                    }
                }
            }
        });
        ClientPlayNetworking.registerListener(SetUsedSlotPacket.SET_USED_SLOT_PACKET, (ctx, buffer) -> {
            ctx.ensureOnMainThread();
            int playerInvIndex = buffer.readInt();
            ((ItemInventoryContainer) Minecraft.getInstance().player.menu).setPlayerInvUsedSlot(playerInvIndex);
        });
        FabricLoader.getInstance().getEntrypoints(MOD_ID + "_client", RegisterQuickShulkerClient.class).forEach(RegisterQuickShulkerClient::registerClient);

        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());

    }
}