package net.kyrptonaught.quickshulker.client;

import malilib.registry.Registry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.config.Configs;
import net.kyrptonaught.quickshulker.network.SetUsedSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.lifecycle.api.client.ClientWorldEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;
import static net.kyrptonaught.quickshulker.QuickShulkerMod.PLAYER_INVENTORY_OFF_HAND_SLOT;

@Environment(EnvType.CLIENT)
public class QuickShulkerModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientWorldEvents.TICK_START.register(clientWorld -> {
            if (Minecraft.getInstance().screen == null && Configs.Options.KEYBIND_OPEN_IN_HAND.getValue()) {
                PlayerEntity player = Minecraft.getInstance().player;
                if (Configs.HotKeys.QUICKSHULKER_KEYBIND.getKeyBind().isKeyBindHeld() && player != null) {
                    if (player.getMainHandStack().isEmpty() && !player.getOffHandStack().isEmpty()) {
                        ClientUtil.tryOpenAndSendPacket(player.getOffHandStack(), PLAYER_INVENTORY_OFF_HAND_SLOT);
                    } else {
                        ClientUtil.tryOpenAndSendPacket(player.getMainHandStack(), player.inventory.selectedSlot);
                    }
                }
            }
        });
        ClientPlayNetworking.registerListener(
            SetUsedSlotPacket.SET_USED_SLOT_PACKET.toString(),
            (client, handler, packetByteBuf) -> {
                int playerInvIndex = packetByteBuf.readInt();
                client.submit(() -> {
                    ((ItemInventoryContainer) client.player.menu).setPlayerInvUsedSlot(playerInvIndex);
                });
                return true;
            }
        );
        FabricLoader.getInstance().getEntrypoints(MOD_ID + "_client", RegisterQuickShulkerClient.class).forEach(RegisterQuickShulkerClient::registerClient);

        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());

    }
}