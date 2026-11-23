package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.minecraft.entity.living.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.PlayerHandActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerUseBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerUseItemC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Opened items should not move. Prevent a malicious client from sending packets to modify inventory items.
// ContainerMixin handles the normal menu click packets sent by vanilla clients.
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Unique
    private static final String ON_SERVER_THREAD = "Lnet/minecraft/network/PacketUtils;ensureOnSameThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/handler/PacketHandler;Lnet/minecraft/util/BlockableEventLoop;)V";

    @Unique
    private boolean isOpenedSlot(int playerInvIndex) {
        ItemInventoryContainer menu = (ItemInventoryContainer) this.player.menu;
        return menu.hasOpenedItem() && menu.getPlayerInvUsedSlot() == playerInvIndex;
    }

    @Unique
    private int handSlot(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? this.player.inventory.selectedSlot : 40;
    }

    @Unique
    private void cancelAndResync(CallbackInfo ci) {
        ci.cancel();
        this.player.onMenuChanged(this.player.menu, this.player.menu.getItems());
    }

    @Inject(method = "handlePlayerHandAction", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handlePlayerHandAction(PlayerHandActionC2SPacket packet, CallbackInfo ci) {
        switch (packet.getAction()) {
            case DROP_ITEM:
            case DROP_ALL_ITEMS:
                if (isOpenedSlot(this.player.inventory.selectedSlot)) cancelAndResync(ci);
                break;
            case SWAP_HELD_ITEMS:
                if (isOpenedSlot(this.player.inventory.selectedSlot) || isOpenedSlot(40)) cancelAndResync(ci);
                break;
            default:
                break;
        }
    }

    @Inject(method = "handlePlayerUseBlock", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handlePlayerUseBlock(PlayerUseBlockC2SPacket packet, CallbackInfo ci) {
        if (isOpenedSlot(handSlot(packet.getHand()))) {
            cancelAndResync(ci);
            BlockPos pos = packet.getPos();
            ServerWorld world = this.player.getServerWorld();
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos.offset(packet.getFace())));
        }
    }

    @Inject(method = "handlePlayerUseItem", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handlePlayerUseItem(PlayerUseItemC2SPacket packet, CallbackInfo ci) {
        if (isOpenedSlot(handSlot(packet.getHand()))) ci.cancel();
    }

    @Inject(method = "handleInteractEntity", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handleInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        InteractionHand hand = packet.getHand();
        if (hand != null && isOpenedSlot(handSlot(hand))) cancelAndResync(ci);
    }

    @Redirect(method = "handleCustomPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerInventory;swapSlotWithHotbar(I)V"))
    private void QS$pickItem(PlayerInventory inventory, int slot) {
        if (!isOpenedSlot(inventory.selectedSlot) && !isOpenedSlot(slot)) {
            inventory.swapSlotWithHotbar(slot);
        }
    }
}
