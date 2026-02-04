package net.kyrptonaught.quickshulker.api;

import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class QuickOpenableRegistry {
    private static final HashMap<Item, QuickShulkerData> quickies = new HashMap<>();

    public static QuickShulkerData getQuickie(Item item) {
        return quickies.get(item);
    }

    public static void register(Item quickItem, QuickShulkerData quickShulkerData) {
        quickies.put(quickItem, quickShulkerData);
    }

    @Deprecated
    public static void register(Item quickItem, Boolean requiresSingularStack, Boolean supportsBundleing, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, supportsBundleing));
    }

    @Deprecated
    public static void register(Item quickItem, Boolean supportsBundleing, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, supportsBundleing));
    }

    @Deprecated
    public static void register(Item quickItem, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, false));
    }

    @SafeVarargs
    @Deprecated
    public static void register(BiConsumer<PlayerEntity, ItemStack> consumer, Item... quickItems) {
        for (Item block : quickItems) {
            register(block, consumer);
        }
    }

    public static class Builder {
        private final List<Item> quickItems = new ArrayList<>();
        private final QuickShulkerData qsdata;

        public Builder() {
            qsdata = new QuickShulkerData();
        }

        public Builder(QuickShulkerData qsdata) {
            this.qsdata = qsdata;
        }

        public void register() {
            for (Item quickItem : quickItems)
                QuickOpenableRegistry.register(quickItem, qsdata);
        }

        @SafeVarargs
        public final Builder setItem(Item... quickItems) {
            this.quickItems.addAll(Arrays.asList(quickItems));
            return this;
        }

        public Builder setOpenAction(BiConsumer<PlayerEntity, ItemStack> openAction) {
            qsdata.openConsumer = openAction;
            return this;
        }

        public Builder supportsBundleing(Boolean supportsBundleing) {
            qsdata.supportsBundleing = supportsBundleing;
            return this;
        }

        public Builder getBundleInv(BiFunction<PlayerEntity, ItemStack, Inventory> getBundleInv) {
            qsdata.bundleInvGetter = getBundleInv;
            return this;
        }

        public Builder canBundleInsertItem(CanBundleInsertItemFunction canBundleInsertItem) {
            qsdata.canBundleInsertItem = canBundleInsertItem;
            return this;
        }

        public Builder canOpenInHand(boolean canOpenInHand) {
            qsdata.canOpenInHand = canOpenInHand;
            return this;
        }

        public Builder ignoreSingleStackCheck(Boolean ignoreSingleStackCheck) {
            qsdata.ignoreSingleStackCheck = ignoreSingleStackCheck;
            return this;
        }
    }
}