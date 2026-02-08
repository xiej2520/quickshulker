package net.kyrptonaught.quickshulker;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.MenuProvider;
import net.minecraft.inventory.menu.ShulkerBoxMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

/// MenuProvider and Inventory for QuickOpen ShulkerBox, without being attached to
/// a block entity
public class FakeShulkerBoxInventory implements Inventory, MenuProvider {
    public final int SIZE = 27;
    private final DefaultedList<ItemStack> inventory = DefaultedList.of(SIZE, ItemStack.EMPTY);
    protected String customName;

    private final ItemStack shulkerBoxItemStack;

    public FakeShulkerBoxInventory(ItemStack shulkerBoxItemStack) {
        this.shulkerBoxItemStack = shulkerBoxItemStack;

        // ShulkerBoxBlockEntity.loadNbt
        NbtCompound blockEntityTag = this.shulkerBoxItemStack.getNbt("BlockEntityTag");
        if (blockEntityTag == null) {
            return;
        }

        if (blockEntityTag.contains("Items", 9)) {
            InventoryHelper.fromNbt(blockEntityTag, this.inventory);
        }

        // CustomName will only be written to the Shulker Box once it is placed and broken
        if (blockEntityTag.contains("CustomName", 8)) {
            this.customName = blockEntityTag.getString("CustomName");
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.shulkerBox";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public Text getDisplayName() {
        return this.hasCustomName() ? new LiteralText(this.getName()) : new TranslatableText(this.getName());
    }

    @Override
    public int getSize() {
        return SIZE;
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
        NbtCompound blockEntityTag = this.shulkerBoxItemStack.getNbt("BlockEntityTag");

        if (this.isEmpty()) {
            this.shulkerBoxItemStack.removeNbt("BlockEntityTag");
            return;
        } else if (blockEntityTag == null) {
            blockEntityTag = this.shulkerBoxItemStack.getOrCreateNbt("BlockEntityTag");
        }

        DefaultedList<ItemStack> itemStacks = DefaultedList.of(SIZE, ItemStack.EMPTY);
        for (int i = 0; i < this.getSize(); i++) {
            itemStacks.set(i, this.getStack(i));
        }
        InventoryHelper.toNbt(blockEntityTag, this.inventory);
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
        this.markDirty();
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


    protected Identifier lootTableId;
    protected long lootTableSeed;

    protected boolean readLootTable(NbtCompound nbt) {
        if (nbt.contains("LootTable", 8)) {
            this.lootTableId = new Identifier(nbt.getString("LootTable"));
            this.lootTableSeed = nbt.getLong("LootTableSeed");
            return true;
        } else {
            return false;
        }
    }

    protected boolean writeLootTable(NbtCompound nbt) {
        if (this.lootTableId != null) {
            nbt.putString("LootTable", this.lootTableId.toString());
            if (this.lootTableSeed != 0L) {
                nbt.putLong("LootTableSeed", this.lootTableSeed);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
        return new ShulkerBoxMenu(playerInventory, this, player);
    }

    @Override
    public String getMenuType() {
        return "minecraft:shulker_box";
    }
}
