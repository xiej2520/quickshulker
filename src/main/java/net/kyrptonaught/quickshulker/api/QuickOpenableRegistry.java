package net.kyrptonaught.quickshulker.api;

import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class QuickOpenableRegistry {
    private static final HashMap<Class<? extends Block>, QuickShulkerData> quickies = new HashMap<>();

    public static QuickShulkerData getQuickie(Item item) {
        if (item instanceof BlockItem) {
            if (quickies.containsKey(((BlockItem) item).getClass()))
                return quickies.get(((BlockItem) item).getClass());
        }
        return quickies.get(item.getClass());
    }

    public static void register(Class<? extends Block> quickItem, QuickShulkerData quickShulkerData) {
        quickies.put(quickItem, quickShulkerData);
    }

    @Deprecated
    public static void register(Class<? extends Block> quickItem, Boolean requiresSingularStack, Boolean supportsBundleing, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, supportsBundleing));
    }

    @Deprecated
    public static void register(Class<? extends Block> quickItem, Boolean supportsBundleing, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, supportsBundleing));
    }

    @Deprecated
    public static void register(Class<? extends Block> quickItem, BiConsumer<PlayerEntity, ItemStack> consumer) {
        register(quickItem, new QuickShulkerData(consumer, false));
    }

    @SafeVarargs
    @Deprecated
    public static void register(BiConsumer<PlayerEntity, ItemStack> consumer, Class<? extends Block>... quickItems) {
        for (Class<? extends Block> block : quickItems) {
            register(block, consumer);
        }
    }

    public static class Builder {
        private final List<Class<? extends Block>> quickItems = new ArrayList<>();
        private final QuickShulkerData qsdata;

        public Builder() {
            qsdata = new QuickShulkerData();
        }

        public Builder(QuickShulkerData qsdata) {
            this.qsdata = qsdata;
        }

        public void register() {
            for (Class<? extends Block> quickItem : quickItems)
                QuickOpenableRegistry.register(quickItem, qsdata);
        }

        @SafeVarargs
        public final Builder setItem(Class<? extends Block>... quickItems) {
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