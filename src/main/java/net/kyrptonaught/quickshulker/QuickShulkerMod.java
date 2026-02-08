package net.kyrptonaught.quickshulker;

import malilib.util.data.ModInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.kyrptonaught.quickshulker.config.ServerConfig;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

import static net.kyrptonaught.quickshulker.config.ServerConfig.CONFIG;


public class QuickShulkerMod implements ModInitializer, RegisterQuickShulker {
    public static final String MOD_ID = "quickshulker";
    public static final String MOD_NAME = "QuickShulker";
    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, MOD_NAME);

    public static final String PACKET_ID = "qs";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "_config.json5"));

    @Override
    public void init() {
        OpenShulkerPacket.registerReceivePacket();
        //QuickBundlePacket.registerReceivePacket();
    }

    // register after bootstrap Blocks in Minecraft.init()
    public void registerProviders() {
        if (CONFIG.quickShulkerBox) {
            new QuickOpenableRegistry.Builder()
                    .setItem(
                            BlockItem.byBlock(Blocks.WHITE_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.SILVER_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.GRAY_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.BLACK_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.BROWN_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.RED_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.ORANGE_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.YELLOW_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.LIME_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.GREEN_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.LIGHT_BLUE_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.CYAN_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.BLUE_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.PURPLE_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.MAGENTA_SHULKER_BOX),
                            BlockItem.byBlock(Blocks.PINK_SHULKER_BOX)
                    )
                    .supportsBundleing(true)
                    .setOpenAction(((player, stack) -> player.openInventoryMenu(
                            new FakeShulkerBoxInventory(stack)
                    )))
                    .register();
        }

        if (CONFIG.quickEnderChest) {
            new QuickOpenableRegistry.Builder(new QuickShulkerData.QuickEnderData())
                    .setItem(BlockItem.byBlock(Blocks.ENDER_CHEST))
                    .supportsBundleing(true)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction((player, stack) ->
                            player.openInventoryMenu(player.getEnderChestInventory()))
                    .register();
        }

        if (CONFIG.quickCraftingTable) {
            new QuickOpenableRegistry.Builder()
                    .setItem(BlockItem.byBlock(Blocks.CRAFTING_TABLE))
                    .ignoreSingleStackCheck(true)
                    // LocalClientPlayerEntity.openMenu
                    .setOpenAction((player, stack) ->
                            // pos used in isValid() only, overridden in CraftingScreenHandlerMixin
                            player.openMenu(new CraftingTableBlock.MenuProvider(player.world, new BlockPos(player.x, player.y, player.z)))

                    )
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
