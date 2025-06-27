package net.kyrptonaught.quickshulker.api;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class QuickOpenableRegistry {
    private static final Map<Item, QuickShulkerData> quickies = new HashMap<>();

    public static QuickShulkerData getQuickie(Item item) {
        return quickies.get(item);
    }

    public static void register(Item quickItem, QuickShulkerData quickShulkerData) {
        quickies.put(quickItem, quickShulkerData);
    }

    public static class Builder {
        private final List<Item> quickItems = new ArrayList<>();
        private final QuickShulkerData qsData;

        public Builder() {
            qsData = new QuickShulkerData();
        }

        public Builder(QuickShulkerData qsdata) {
            this.qsData = qsdata;
        }

        public void register() {
            for (Item quickItem : quickItems) {
                QuickOpenableRegistry.register(quickItem, qsData);
            }
        }

        public final Builder setItem(Item... quickItems) {
            this.quickItems.addAll(Arrays.asList(quickItems));
            return this;
        }

        public Builder setOpenAction(BiConsumer<PlayerEntity, ItemStack> openAction) {
            qsData.openConsumer = openAction;
            return this;
        }

        public Builder supportsBundleing(Boolean supportsBundleing) {
            qsData.supportsBundleing = supportsBundleing;
            return this;
        }

        public Builder getBundleInv(BiFunction<PlayerEntity, ItemStack, Inventory> getBundleInv) {
            qsData.bundleInvGetter = getBundleInv;
            return this;
        }

        public Builder canBundleInsertItem(CanBundleInsertItemFunction canBundleInsertItem) {
            qsData.canBundleInsertItem = canBundleInsertItem;
            return this;
        }

        public Builder canOpenInHand(boolean canOpenInHand) {
            qsData.canOpenInHand = canOpenInHand;
            return this;
        }

        public Builder ignoreSingleStackCheck(Boolean ignoreSingleStackCheck) {
            qsData.ignoreSingleStackCheck = ignoreSingleStackCheck;
            return this;
        }
    }
}