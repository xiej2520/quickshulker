package net.kyrptonaught.quickshulker;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

/// MenuProvider and Inventory for QuickOpen EnderChest, without being attached to
/// a block entity
/// does this need to be a menuprovider?
public class FakeEnderChestInventory implements Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.of(27, ItemStack.EMPTY);

    @Override
    public String getName() {
        return new TranslatableText("container.enderchest").getString();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.enderchest");
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = InventoryHelper.split(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStackQuietly(int slot) {
        return InventoryHelper.remove(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getSize() > this.getMaxStackSize()) {
            stack.setSize(this.getMaxStackSize());
        }

        this.markDirty();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void markDirty() {
        // no-op
    }

    @Override
    public boolean isValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        // no-op
    }

    @Override
    public void onClose(PlayerEntity player) {
        // no-op
    }

    @Override
    public boolean canSetStack(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public int getData(int id) {
        return 0;
    }

    @Override
    public void setData(int id, int value) {
        // no-op
    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
