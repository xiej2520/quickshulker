package net.kyrptonaught.shulkerutils;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class ShulkerUtils {
    public static boolean isShulkerItem(ItemStack item) {
        return Block.byItem(item.getItem()) instanceof ShulkerBoxBlock;
    }

    public static boolean shulkerContainsAny(Inventory shulkerInv, ItemStack stack) {
        for (int i = 0; i < shulkerInv.getSize(); i++) {
            if (shulkerInv.getStack(i).getItem().equals(stack.getItem()))
                return true;
        }
        return false;
    }

    public static ItemStack insertIntoShulker(SimpleInventory shulkerInv, ItemStack stack, PlayerEntity player) {
        if (isShulkerItem(stack) || !canInsert(shulkerInv, stack))
            return stack;
        ItemStack output = shulkerInv.addStack(stack);
        shulkerInv.onClose(player);
        return output;
    }

    public static ItemStackInventory getInventoryFromShulker(ItemStack stack) {
        Block shulker = ((BlockItem) stack.getItem()).getBlock();
        if (shulker instanceof UpgradableShulker) {
            return new ItemStackInventory(stack, ((UpgradableShulker) shulker).getInventorySize());
        }
        return new ItemStackInventory(stack, 27);
    }

    public static boolean canInsert(SimpleInventory shulkerInv, ItemStack stack) {
        boolean bl = false;

        for (ItemStack itemStack : shulkerInv.stacks) {
            if (itemStack.isEmpty() || canCombine(itemStack, stack) && itemStack.getSize() < itemStack.getMaxSize()) {
                bl = true;
                break;
            }
        }

        return bl;
    }

    public static boolean canCombine(ItemStack one, ItemStack two) {
        return one.getItem() == two.getItem() && ItemStack.matchesNbt(one, two);
    }
}

