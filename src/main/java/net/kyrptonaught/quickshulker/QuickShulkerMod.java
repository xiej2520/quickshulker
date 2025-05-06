package net.kyrptonaught.quickshulker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.AddNonConflictingKeyBind;
import net.kyrptonaught.kyrptconfig.config.NonConflicting.NonConflictingKeyBindData;
import net.kyrptonaught.quickshulker.api.ItemStackInventory;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;
import net.kyrptonaught.quickshulker.api.Util;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.client.util.InputUtil;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;


public class QuickShulkerMod implements ModInitializer, RegisterQuickShulker {
    public static final String MOD_ID = "quickshulker";
    public static ConfigManager.SingleConfigManager config = new ConfigManager.SingleConfigManager(MOD_ID, new ConfigOptions());
    public static double lastMouseX, lastMouseY;

    @Override
    public void onInitialize() {
        config.load();
        OpenShulkerPacket.registerReceivePacket();
        QuickBundlePacket.registerReceivePacket();
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getMainHandStack();
            if (!world.isClient) {
                if (QuickShulkerMod.getConfig().rightClickToOpen) {
                    if (Util.isOpenableItem(stack)) {
                        if (hand == Hand.MAIN_HAND)
                            Util.openItem(player, 0, player.getInventory().selectedSlot);
                        else Util.openItem(player, 0, PlayerInventory.OFF_HAND_SLOT);

                        return TypedActionResult.success(stack);
                    }
                }
            }
            return TypedActionResult.pass(stack);
        });
        FabricLoader.getInstance().getEntrypoints(MOD_ID, RegisterQuickShulker.class).forEach(RegisterQuickShulker::registerProviders);
    }

    @Override
    public void registerProviders() {
        QuickOpenableRegistry.register(
            ShulkerBoxBlock.class,
            (player, stack) -> player.openContainer(
                new SimpleNamedContainerFactory(
                    (i, playerInventory, playerEntity) -> new ShulkerBoxContainer(i, player.inventory, new ItemStackInventory(stack, 27)),
                    new TranslatableText("container.shulkerBox")
                )
            )
        );

        QuickOpenableRegistry.register(
            EnderChestBlock.class,
            (player, stack) -> player.openContainer(
                new SimpleNamedContainerFactory(
                    (i, playerInventory, playerEntity) -> GenericContainer.createGeneric9x3(i, playerInventory, player.getEnderChestInventory()),
                    new TranslatableText("container.enderchest")
                )
            )
        );

        QuickOpenableRegistry.register(
            CraftingTableBlock.class,
            (player, stack) -> player.openContainer(
                new SimpleNamedContainerFactory(
                    (i, playerInventory, playerEntity) -> new CraftingTableContainer(i, player.inventory),
                    new TranslatableText("container.crafting")
                )
            )
        );
        if (getConfig().quickShulkerBox)
            QuickOpenableRegistry.register(ShulkerBoxBlock.class, true, ((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    new ShulkerBoxScreenHandler(i, player.getInventory(), new ItemStackInventory(stack, 27)), stack.hasCustomName() ? stack.getName() : new TranslatableText("container.shulkerBox")))));

        if (getConfig().quickEChest) {
            QuickShulkerData.QuickEnderData enderData = new QuickShulkerData.QuickEnderData(((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, player.getEnderChestInventory()), new TranslatableText("container.enderchest")))), true);
            QuickOpenableRegistry.register(EnderChestBlock.class, enderData);
        }
        if (getConfig().quickCraftingTables)
            QuickOpenableRegistry.register(CraftingTableBlock.class, ((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    new CraftingScreenHandler(i, playerInventory, ScreenHandlerContext.create(player.getEntityWorld(), player.getBlockPos())), new TranslatableText("container.crafting")))));

        if (getConfig().quickStonecutter)
            QuickOpenableRegistry.register(StonecutterBlock.class, ((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    new StonecutterScreenHandler(i, playerInventory, ScreenHandlerContext.create(player.getEntityWorld(), player.getBlockPos())), new TranslatableText("container.stonecutter")))));


        // 1.16 code
        //QuickOpenableRegistry.register(CraftingTableBlock.class, ((player, stack) -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
        //        new CraftingScreenHandler(i, playerInventory, ScreenHandlerContext.EMPTY), new TranslatableText("container.crafting")))));
    }

    @Override
    public void addKeyBinding(List<NonConflictingKeyBindData> list) {
        InputUtil.KeyCode key = InputUtil.fromName(getConfig().keybinding);
        // in 1.16:
        // key.getKeyCode() -> key.getCode()
        // setKey.getName() -> setKey.getTranslationKey()
        NonConflictingKeyBindData bindData = new NonConflictingKeyBindData("key.quickshulker.config.keybinding", "key.categories.quickshulker", key.getCategory(), key.getKeyCode(), setKey -> {
            getConfig().keybinding = setKey.getName();
            config.save();
            ClientUtil.keycode = null;
        });
        list.add(bindData);
    }
}