package net.kyrptonaught.quickshulker;

import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.menu.CraftingTableScreen;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.World;
import net.ornithemc.osl.entrypoints.api.ModInitializer;


public class QuickShulkerMod implements ModInitializer, RegisterQuickShulker {
    public static final String MOD_ID = "quickshulker";
    public static final String PACKET_ID = "qs";
    //public static ConfigManager.SingleConfigManager config = new ConfigManager.SingleConfigManager(MOD_ID, new ConfigOptions());
    public static double lastMouseX, lastMouseY;

    @Override
    public void init() {
        //config.load();
        OpenShulkerPacket.registerReceivePacket();
        //QuickBundlePacket.registerReceivePacket();

        FabricLoader.getInstance().getEntrypoints(MOD_ID, RegisterQuickShulker.class).forEach(RegisterQuickShulker::registerProviders);
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

    @Override
    public void registerProviders() {
        if (getConfig().quickShulkerBox) {
            new QuickOpenableRegistry.Builder()
                    .setItem(ShulkerBoxBlock.class)
                    .supportsBundleing(true)
                    /*
                            .setOpenAction(((player, stack) -> player.openMenu(new EmptyMenuProvider((i, playerInventory, playerEntity) ->
                                    new ShulkerBoxMenu(i, player.inventory, new ItemStackInventory(stack, 27)), stack.hasCustomName() ? stack.getName() : new TranslatableText("container.shulkerBox")))))
                     */
                    .setOpenAction(((player, stack) -> player.openMenu(
                            new ShulkerBoxBlockEntity()
                    )))
                    .register();
        }

        if (getConfig().quickEnderChest) {
            new QuickOpenableRegistry.Builder(new QuickShulkerData.QuickEnderData())
                    .setItem(EnderChestBlock.class)
                    .supportsBundleing(true)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction((player, stack) -> player.openInventoryMenu(new FakeEnderChestInventory()))
                    .register();
        }

        if (getConfig().quickCraftingTables) {
            new QuickOpenableRegistry.Builder()
                    .setItem(CraftingTableBlock.class)
                    .ignoreSingleStackCheck(true)
                    // LocalClientPlayerEntity.openMenu
                    .setOpenAction((player, stack) -> Minecraft.getInstance().openScreen(new CraftingTableScreen(player.inventory, player.world)))
                    .register();
        }

        //if (getConfig().quickStonecutter)
        //    new QuickOpenableRegistry.Builder()
        //            .setItem(StonecutterBlock.class)
        //            .ignoreSingleStackCheck(true)
        //            .setOpenAction(((player, stack) -> player.openContainer(new SimpleNamedContainerFactory((i, playerInventory, playerEntity) ->
        //                    new StonecutterContainer(i, playerInventory, BlockContext.create(player.getEntityWorld(), player.getBlockPos())), new TranslatableText("container.stonecutter")))))
        //            .register();
    }
}
