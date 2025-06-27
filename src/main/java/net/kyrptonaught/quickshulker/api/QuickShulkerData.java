package net.kyrptonaught.quickshulker.api;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class QuickShulkerData {
    public BiConsumer<PlayerEntity, ItemStack> openConsumer;
    BiFunction<PlayerEntity, ItemStack, Inventory> bundleInvGetter;
    CanBundleInsertItemFunction canBundleInsertItem;

    public boolean supportsBundleing = false;
    public boolean ignoreSingleStackCheck = false;
    public boolean canOpenInHand = true;

    public QuickShulkerData() {

    }

    public QuickShulkerData(BiConsumer<PlayerEntity, ItemStack> openConsumer, Boolean supportsBundleing) {
        this.openConsumer = openConsumer;
        this.supportsBundleing = supportsBundleing;
    }

    public QuickShulkerData(BiConsumer<PlayerEntity, ItemStack> openConsumer, Boolean supportsBundleing, Boolean ignoreSingleStackCheck) {
        this.openConsumer = openConsumer;
        this.supportsBundleing = supportsBundleing;
        this.ignoreSingleStackCheck = ignoreSingleStackCheck;
    }

    public static class QuickEnderData extends QuickShulkerData {
        public QuickEnderData() {
            super();
            canBundleInsertItem = CanBundleInsertItemFunction.ALWAYS;
        }

        public QuickEnderData(BiConsumer<PlayerEntity, ItemStack> openConsumer, Boolean supportsBundleing) {
            super(openConsumer, supportsBundleing);
            canBundleInsertItem = CanBundleInsertItemFunction.ALWAYS;
        }

        public QuickEnderData(BiConsumer<PlayerEntity, ItemStack> openConsumer, Boolean supportsBundleing, Boolean ignoreSingleStackCheck) {
            super(openConsumer, supportsBundleing, ignoreSingleStackCheck);
            canBundleInsertItem = CanBundleInsertItemFunction.ALWAYS;
        }

        public Inventory getInventory(PlayerEntity player, ItemStack stack) {
            if (!QuickShulkerMod.getConfig().quickEnderChest) {
                return null;
            }
            return player.getEnderChestInventory();
        }
    }
}