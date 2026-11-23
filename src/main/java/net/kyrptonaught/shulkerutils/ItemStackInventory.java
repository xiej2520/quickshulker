package net.kyrptonaught.shulkerutils;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class ItemStackInventory extends SimpleContainer {
    protected final ItemStack itemStack;
    protected final int SIZE;

    public ItemStackInventory(ItemStack stack, int SIZE) {
        super(getStacks(stack, SIZE).toArray(new ItemStack[SIZE]));
        itemStack = stack;
        this.SIZE = SIZE;
    }

    public static NonNullList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        CompoundTag compoundTag = usedStack.getTagElement("BlockEntityTag");
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        if (compoundTag != null && compoundTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(compoundTag, itemStacks);
        }
        return itemStacks;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // Keep BlockEntityTag and Items always, writes empty list when empty, like Vanilla breaking
        // empty box (see shulker_box loot table `set_contents`).
        CompoundTag compoundTag = itemStack.getOrCreateTagElement("BlockEntityTag");

        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < getContainerSize(); i++) {
            itemStacks.set(i, getItem(i));
        }
        ContainerHelper.saveAllItems(compoundTag, itemStacks);
    }

    public boolean shouldDeleteNBT(CompoundTag blockEntityTag) {
        if (!blockEntityTag.contains("Items"))
            return blockEntityTag.getAllKeys().size() == 0;
        return isEmpty();
    }

    @Override
    public void stopOpen(Player playerEntity_1) {
        if (itemStack.getCount() > 1) {
            int count = itemStack.getCount();
            itemStack.setCount(1);
            playerEntity_1.addItem(new ItemStack(itemStack.getItem(), count - 1));
        }
        setChanged();
        // itemStack.removeSubTag(QuickShulkerMod.MOD_ID);
    }
}
