package net.kyrptonaught.shulkerutils;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DefaultedList;


public class ItemStackInventory extends SimpleInventory {
    protected final ItemStack itemStack;
    protected final int SIZE;

    public ItemStackInventory(ItemStack stack, int SIZE) {
        super("ItemStackInventory", false, SIZE);

        //ItemStack[] itemStacks = getStacks(stack, SIZE).toArray(new ItemStack[SIZE]);
        itemStack = stack;
        this.SIZE = SIZE;
    }

    public static DefaultedList<ItemStack> getStacks(ItemStack usedStack, int SIZE) {
        NbtCompound compoundTag = usedStack.getNbt("BlockEntityTag");
        DefaultedList<ItemStack> itemStacks = DefaultedList.of(SIZE, ItemStack.EMPTY);
        if (compoundTag != null && compoundTag.contains("Items", 9)) {
            InventoryHelper.fromNbt(compoundTag, itemStacks);
        }
        return itemStacks;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        NbtCompound compoundTag = itemStack.getNbt("BlockEntityTag");
        if (this.isEmpty()) {
            itemStack.removeNbt("BlockEntityTag");
            return;
        } else if (compoundTag == null) {
            compoundTag = itemStack.getOrCreateNbt("BlockEntityTag");
        }

        DefaultedList<ItemStack> itemStacks = DefaultedList.of(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < this.getSize(); i++) {
            itemStacks.set(i, this.getStack(i));
        }
        InventoryHelper.toNbt(compoundTag, itemStacks);
    }

    public boolean shouldDeleteNBT(NbtCompound blockEntityTag) {
        if (!blockEntityTag.contains("Items"))
            return blockEntityTag.getKeys().isEmpty();
        return this.isEmpty();
    }

    @Override
    public void onClose(PlayerEntity playerEntity) {
        int count = itemStack.getSize();
        if (count > 1) {
            itemStack.setSize(1);
            playerEntity.addItem(new ItemStack(itemStack.getItem(), count - 1));
        }
        markDirty();
        // itemStack.removeSubTag(QuickShulkerMod.MOD_ID);
    }
}
