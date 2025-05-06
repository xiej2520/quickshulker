package net.kyrptonaught.shulkerutils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;


public class ItemStackInventory extends BasicInventory {
    protected final ItemStack itemStack;
    protected final int SIZE;

    public ItemStackInventory(ItemStack stack, int SIZE) {
        super(getStacks(stack, SIZE).toArray(new ItemStack[SIZE]));
        itemStack = stack;
        this.SIZE = SIZE;
    }

    public static DefaultedList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        CompoundTag compoundTag = usedStack.getSubTag("BlockEntityTag");
        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
        if (compoundTag != null && compoundTag.contains("Items", 9)) {
            Inventories.fromTag(compoundTag, itemStacks);
        }
        return itemStacks;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
        if (isInvEmpty()) {
            itemStack.removeSubTag("BlockEntityTag");
            return;
        } else if (compoundTag == null) {
            compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
        }

        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < getInvSize(); i++) {
            itemStacks.set(i, getInvStack(i));
        }
        Inventories.toTag(compoundTag, itemStacks);
    }

    public boolean shouldDeleteNBT(CompoundTag blockEntityTag) {
        if (!blockEntityTag.contains("Items"))
            return blockEntityTag.getKeys().size() == 0;
        return isInvEmpty();
    }

    @Override
    public void onInvClose(PlayerEntity playerEntity_1) {
        if (itemStack.getCount() > 1) {
            int count = itemStack.getCount();
            itemStack.setCount(1);
            playerEntity_1.giveItemStack(new ItemStack(itemStack.getItem(), count - 1));
        }
        markDirty();
        // itemStack.removeSubTag(QuickShulkerMod.MOD_ID);
    }
}
