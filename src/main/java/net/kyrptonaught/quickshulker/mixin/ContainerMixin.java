package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Unique
    ItemStack openededStack;

    @Shadow
    @Final
    public List<Slot> slots;


    @Shadow
    @Final
    public int syncId;


    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    public void QS$onClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (slotId > 0 && slotId < slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot != null && slot.inventory instanceof PlayerInventory)
                if (hasItem()) {
                    if (Util.areItemsEqual(slot.getStack(), getOpenedItem())) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        if (player instanceof ServerPlayerEntity) {
                            ServerPlayerEntity sPlayer = (ServerPlayerEntity) player;
                            // 1.16: ContainerSlotUpdateS2CPacket -> ScreenHandlerSlotUpdateS2CPacket
                            sPlayer.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(syncId, slotId, slot.getStack()));
                        }
                    }
                }
        }
    }

    @Unique
    @Override
    public ItemStack getOpenedItem() {
        return openededStack;
    }

    @Unique
    @Override
    public void setOpenedItem(ItemStack openedItem) {
        if (!Util.isEnderChest(openedItem))
            this.openededStack = openedItem;
    }
}
