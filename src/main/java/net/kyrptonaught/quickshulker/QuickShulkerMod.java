package net.kyrptonaught.quickshulker;

import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.World;
import net.ornithemc.osl.entrypoints.api.ModInitializer;


public class QuickShulkerMod implements ModInitializer {
    public static final String MOD_ID = "quickshulker";
    public static final String PACKET_ID = "qs";
    //public static ConfigManager.SingleConfigManager config = new ConfigManager.SingleConfigManager(MOD_ID, new ConfigOptions());
    public static double lastMouseX, lastMouseY;

    @Override
    public void init() {
        //config.load();
        OpenShulkerPacket.registerReceivePacket();
        //QuickBundlePacket.registerReceivePacket();
    }

    // move to Server/ClientPlayerInteractionManagerMixin due to lack of OSL event
    public static InteractionResultHolder<ItemStack> interactItem(PlayerEntity player, World world, InteractionHand hand) {
        ItemStack stack = player.getHandStack(hand);
        if (!world.isClient) {
            if (QuickShulkerMod.getConfig().rightClickToOpen) {
                if (Util.isOpenableItem(stack) && Util.canOpenInHand(stack)) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        Util.openItem(player, 0, player.inventory.selectedSlot);
                    } else {
                        final int PLAYER_INVENTORY_OFF_HAND_SLOT = 40;
                        Util.openItem(player, 0, PLAYER_INVENTORY_OFF_HAND_SLOT);
                    }

                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
                }
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);

    }

    public static ConfigOptions getConfig() {
        //return (ConfigOptions) config.getConfig();
        return new ConfigOptions();
    }
}
