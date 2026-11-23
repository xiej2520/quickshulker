package net.kyrptonaught.quickshulker.client;

import blue.endless.jankson.Comment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.kyrptconfig.keybinding.CustomKeyBinding;
import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.network.SetUsedSlotPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.MOD_ID;

@Environment(EnvType.CLIENT)
public class QuickShulkerModClient implements ClientModInitializer {

    @Comment("Activation key")
    public static CustomKeyBinding keybinding = CustomKeyBinding.configDefault(MOD_ID, "key.keyboard.k");

    public static CustomKeyBinding getKeybinding() {
        return keybinding;
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
            boolean pressed = getKeybinding().wasPressed();
            if (pressed && Minecraft.getInstance().screen == null && QuickShulkerMod.getConfig().keybind) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty()) {
                        ClientUtil.CheckAndSend(player.getOffhandItem(), 45);
                    } else {
                        ClientUtil.CheckAndSend(player.getMainHandItem(), 36 + player.inventory.selected);
                    }
                }
            }
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (world.isClientSide && QuickShulkerMod.getConfig().rightClickToOpen && Util.canOpenInHand(stack)) {
                int slot = hand == InteractionHand.MAIN_HAND ? 36 + player.inventory.selected : 45;
                if (ClientUtil.CheckAndSend(stack, slot)) {
                    return InteractionResultHolder.success(stack);
                }
            }
            return InteractionResultHolder.pass(stack);
        });
        ClientPlayNetworking.registerGlobalReceiver(SetUsedSlotPacket.SET_USED_SLOT_PACKET, (client, handler, packetByteBuf, sender) -> {
            int playerInvIndex = packetByteBuf.readInt();
            client.execute(() -> {
                ((ItemInventoryContainer) client.player.containerMenu).setPlayerInvUsedSlot(playerInvIndex);
            });
        });
        FabricLoader.getInstance().getEntrypoints(MOD_ID + "_client", RegisterQuickShulkerClient.class).forEach(RegisterQuickShulkerClient::registerClient);

        KeyBindingHelper.registerKeyBinding(new DisplayOnlyKeyBind(
                "key.quickshulker.config.keybinding",
                "key.categories.quickshulker",
                getKeybinding(),
                setKey -> {
                }
        ));
    }
}