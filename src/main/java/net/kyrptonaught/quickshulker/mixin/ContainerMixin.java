package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(Container.class)
public abstract class ContainerMixin implements ItemInventoryContainer {

    @Shadow
    @Final
    public List<Slot> slots;

    // index of the currently opened QuickShulker item in the player inventory
    // different from index of the item in the ContainerScreen/HandledScreen,
    // which is the player inventory, may or may not be combined with another screen
    @Unique
    int playerInvSlot = -1;

    @Unique
    public int getUsedSlotInPlayerInv() {
        return playerInvSlot;
    }

    @Unique
    public void setUsedSlot(int playerInvSlotID) {
        this.playerInvSlot = playerInvSlotID;
    }

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    public void QS$onClick(int slotId, int button, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        // need to prevent the opened QuickShulker item from being moved in the inventory
        // see issue #28
        if (slotId > 0 && slotId < slots.size()) {
            if (hasItem()) {
                if (slots.get(slotId).inventory instanceof PlayerInventory && ((SlotAccessor) slots.get(slotId)).getIndex() == playerInvSlot) {
                    // TODO
                    // causes desyncs but I don't think it loses items?
                    // Intended behavior: inventory actions on opened QuickShulker item doesn't move the item at all
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
