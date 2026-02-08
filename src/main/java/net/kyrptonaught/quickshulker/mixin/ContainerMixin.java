package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.ActionType;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(InventoryMenu.class)
public abstract class ContainerMixin implements ItemInventoryContainer {

    @Shadow
    public List<InventorySlot> slots;

    // index of the currently opened QuickShulker item in the player inventory
    // different from index of the item in the ContainerScreen/HandledScreen,
    // which is the player inventory, may or may not be combined with another screen
    @Unique
    int playerInvUsedSlot = -1;

    @Unique
    @Override
    public int getPlayerInvUsedSlot() {
        return playerInvUsedSlot;
    }

    @Unique
    @Override
    public void setPlayerInvUsedSlot(int playerInvUsedSlot) {
        this.playerInvUsedSlot = playerInvUsedSlot;
    }

    @Unique
    public ItemStack getPlayerInvUsedStack(PlayerEntity playerEntity) {
        return playerInvUsedSlot >= 0 ? playerEntity.inventory.getStack(playerInvUsedSlot) : ItemStack.EMPTY;
    }

    @Unique
    public boolean isUsedSlot(int slotId) {
        return this.slots.get(slotId).inventory instanceof PlayerInventory && ((SlotAccessor) slots.get(slotId)).getInventoryIndex() == playerInvUsedSlot;
    }

    @Inject(method = "onClickSlot", at = @At("HEAD"), cancellable = true)
    public void QS$onClick(int slotId, int button, ActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        // need to prevent the opened QuickShulker item from being moved in the inventory
        // see issue #28
        if (slotId > 0 && slotId < slots.size() && hasOpenedItem()) {
            // Intended behavior: inventory actions on opened QuickShulker item doesn't move the item at all
            if (isUsedSlot(slotId)) {
                cir.setReturnValue(ItemStack.EMPTY);
            } else if (slotActionType == ActionType.SWAP && button == playerInvUsedSlot) {
                // QuickShulker item can be moved but slotId doesn't refer to it for SWAP
                cir.setReturnValue(ItemStack.EMPTY);
            } else if (slotActionType == ActionType.PICKUP_ALL) {
                // stop picking up all items equivalent to QuickShulker item
                ItemStack cursorStack = playerEntity.inventory.getCursorStack();
                if (ItemStack.matchesItemIgnoreDamage(cursorStack, getPlayerInvUsedStack(playerEntity))) {
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }

    // onSlotClick notes
    // QUICK_CRAFT: drag stack between slots, always return ItemStack.EMPTY
    // PICKUP: clicking on stack,
    //   slotId == -999 (outside container): drop all, return ItemStack.EMPTY
    //   slotId < 0: ItemStack.EMPTY
    //   else
    //     slot stack not empty, **itemStack = slot stack**
    //     slotId stack empty, cursor stack not empty, can insert cursor stack => add stack (left mouse) or 1 item (right mouse). return ItemStack.EMPTY
    //     slotId stack not empty, player can take items:
    //       cursor stack empty: take items, return ItemStack.EMPTY
    //       cursor stack not empty, can insert to slot: logic, return ItemStack.EMPTY
    // QUICK_MOVE: shift-click
    //   mousebutton is left or right
    //     slotId < 0: ItemStack.EMPTY
    //     slot empty or can't take out: ItemStack.EMPTY
    //     else shift items out of slot, **return leftover**
    // SWAP: exchange using 1-9 key
    //   swap items, return ItemStack.EMPTY
    // CLONE: in creative (usually middle click over stack), return ItemStack.EMPTY
    // THROW: Q, return ItemStack.EMPTY
    // PICKUP_ALL: double click, return ItemStack.EMPTY

    // always return ItemStack.EMPTY, except when picking up [= existing item], quick move [= leftover]
}
