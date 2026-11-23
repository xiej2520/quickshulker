package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMixin implements ItemInventoryContainer {

    @Shadow
    @Final
    public List<Slot> slots;

    // index of the currently opened QuickShulker item in the player inventory
    // different from index of the item in the ContainerScreen/HandledScreen,
    // which is the player inventory, may or may not be combined with another screen
    @Unique
    int playerInvUsedSlot = -1;

    @Unique
    public int getPlayerInvUsedSlot() {
        return playerInvUsedSlot;
    }

    @Unique
    public void setPlayerInvUsedSlot(int playerInvUsedSlot) {
        this.playerInvUsedSlot = playerInvUsedSlot;
    }

    @Unique
    ItemStack openedStack = ItemStack.EMPTY;

    @Unique
    public ItemStack getOpenedStack() {
        return openedStack;
    }

    @Unique
    public void setOpenedStack(ItemStack openedStack) {
        this.openedStack = openedStack;
    }

    @Unique
    public ItemStack getPlayerInvUsedStack(Player playerEntity) {
        return playerInvUsedSlot >= 0 ? playerEntity.inventory.getItem(playerInvUsedSlot) : ItemStack.EMPTY;
    }

    @Unique
    public boolean isUsedSlot(int slotId) {
        return this.slots.get(slotId).container instanceof Inventory && ((SlotAccessor) slots.get(slotId)).getIndex() == playerInvUsedSlot;
    }

    // slotId counts from 0 at the top left of the menu (possibly in the opened container)
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    public void QS$onClick(int slotId, int button, ClickType slotActionType, Player playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        // prevent items from being taken out of the menu (dupe) if anything moved or used up the opened stack.
        if (hasOpenedItem() && playerEntity instanceof ServerPlayer) {
            ItemStack current = playerEntity.inventory.getItem(playerInvUsedSlot);
            if (current != openedStack || current.isEmpty()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        }
        // need to prevent the opened QuickShulker item from being moved in the inventory
        // see issue #28
        if (slotId >= 0 && slotId < slots.size() && hasOpenedItem()) {
            // Intended behavior: inventory actions on opened QuickShulker item doesn't move the item at all
            if (isUsedSlot(slotId)) {
                cir.setReturnValue(ItemStack.EMPTY);
            } else if (slotActionType == ClickType.SWAP && button == playerInvUsedSlot) {
                // QuickShulker item can be moved but slotId doesn't refer to it for SWAP
                cir.setReturnValue(ItemStack.EMPTY);
            } else if (slotActionType == ClickType.PICKUP_ALL) {
                // stop picking up all items equivalent to QuickShulker item
                ItemStack cursorStack = playerEntity.inventory.getCarried();
                if (ItemStack.isSame(cursorStack, getPlayerInvUsedStack(playerEntity))) {
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
