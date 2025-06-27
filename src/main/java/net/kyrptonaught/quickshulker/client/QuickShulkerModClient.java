package net.kyrptonaught.quickshulker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.quickshulker.Comment;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.network.SetUsedSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.menu.SurvivalInventoryScreen;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import net.ornithemc.osl.lifecycle.api.client.ClientWorldEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;

@Environment(EnvType.CLIENT)
public class QuickShulkerModClient implements ClientModInitializer {

    @Comment("Activation key")
    public static CustomKeyBinding keybinding = CustomKeyBinding.configDefault(MOD_ID, "key.keyboard.k");
    public static ConfigManager.SingleConfigManager keybind_config = new ConfigManager.SingleConfigManager(MOD_ID + "_keybind", new ConfigOptions());

    public static CustomKeyBinding getKeybinding() {
        return keybinding;
    }

    @Override
    public void initClient() {
        keybind_config.load();

        ClientWorldEvents.TICK_START.register(clientWorld -> {
            if (Minecraft.getInstance().screen == null && QuickShulkerMod.getConfig().keybind) {
                PlayerEntity player = Minecraft.getInstance().player;
                if (getKeybinding().isKeybindPressed() && player != null) {
                    if (player.getMainHandStack().isEmpty() && !player.getOffHandStack().isEmpty()) {
                        ClientUtil.CheckAndSend(player.getOffHandStack(), 45);
                    } else {
                        ClientUtil.CheckAndSend(player.getMainHandStack(), 36 + player.inventory.selectedSlot);
                    }
                }
            }
        });
        ClientPlayNetworking.registerListener(OpenInventoryPacket.OPEN_INV.toString(), (client, handler, packet) -> {
            client.submit(() -> {
                client.openScreen(new SurvivalInventoryScreen(client.player));
            });
            return true;
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

        KeyBindingEvents.REGISTER_KEYBINDS.register(registry -> new DisplayOnlyKeyBind(
                "key.quickshulker.config.keybinding",
                "key.categories.quickshulker",
                getKeybinding(),
                setKey -> keybind_config.save()
        ));
    }
}