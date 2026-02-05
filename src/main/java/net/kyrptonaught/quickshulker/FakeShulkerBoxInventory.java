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
/// does this need to be a menuprovider?
public class FakeShulkerBoxInventory implements Inventory, MenuProvider {
    private final DefaultedList<ItemStack> inventory = DefaultedList.of(27, ItemStack.EMPTY);
    protected String customName;

    ItemStack shulkerBoxItemStack;

    public FakeShulkerBoxInventory(ItemStack shulkerBoxItemStack) {
        this.shulkerBoxItemStack = shulkerBoxItemStack;

        // ShulkerBoxBlockEntity loadNbt
        NbtCompound nbt = this.shulkerBoxItemStack.getNbt();
        if (nbt == null || !nbt.contains("BlockEntityTag", 10)) {
            return;
        }

        NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");

        if (!this.readLootTable(nbt) && blockEntityTag.contains("Items", 9)) {
            InventoryHelper.fromNbt(blockEntityTag, this.inventory);
        }

        if (blockEntityTag.contains("CustomName", 8)) {
            this.customName = blockEntityTag.getString("CustomName");
        } else {
            this.customName = shulkerBoxItemStack.getHoverName();
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
        this.saveNbt();
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

    public void saveNbt() {
        NbtCompound nbt = this.shulkerBoxItemStack.getNbt();
        if (nbt == null || !nbt.contains("BlockEntityTag", 10)) {
            return;
        }
        NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");

        if (!this.writeLootTable(blockEntityTag)) {
            InventoryHelper.toNbt(blockEntityTag, this.inventory);
        }

        if (this.hasCustomName()) {
            blockEntityTag.putString("CustomName", this.customName);
        }
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
