package net.kyrptonaught.quickshulker.api;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.MenuProvider;
import net.minecraft.text.Text;

public class SimpleMenuProvider implements MenuProvider  {

    public SimpleMenuProvider() {

    }

    @Override
    public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public String getMenuType() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public Text getDisplayName() {
        return null;
    }
}
