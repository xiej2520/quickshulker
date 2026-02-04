package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.menu.CraftingTableScreen;
import net.minecraft.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.getConfig;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitComplete(CallbackInfo ci)
    {
        registerProviders();
    }


    @Unique
    private void registerProviders() {
        if (getConfig().quickShulkerBox) {
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
                    .setItem(BlockItem.byBlock(Blocks.ENDER_CHEST))
                    .supportsBundleing(true)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction((player, stack) ->
                            player.openInventoryMenu(player.getEnderChestInventory()))
                    .register();
        }

        if (getConfig().quickCraftingTables) {
            new QuickOpenableRegistry.Builder()
                    .setItem(BlockItem.byBlock(Blocks.CRAFTING_TABLE))
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
