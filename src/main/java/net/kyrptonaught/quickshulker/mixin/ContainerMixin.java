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
        if (slotId > 0 && slotId < slots.size()) {
            if (hasItem()) {
                if (slots.get(slotId).inventory instanceof PlayerInventory && ((SlotAccessor) slots.get(slotId)).getIndex() == playerInvSlot) {
                    // TODO
                    // not sure if this is correct
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }
}