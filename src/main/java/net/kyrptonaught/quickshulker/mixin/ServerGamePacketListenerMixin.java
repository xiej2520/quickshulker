package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Opened items should not move. Prevent a malicious client from sending packets to modify inventory items.
// ContainerMixin handles the normal menu click packets sent by vanilla clients.
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @Shadow
    public ServerPlayer player;

    @Unique
    private static final String ON_SERVER_THREAD = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V";

    @Unique
    private boolean isOpenedSlot(int playerInvIndex) {
        ItemInventoryContainer menu = (ItemInventoryContainer) this.player.containerMenu;
        return menu.hasOpenedItem() && menu.getPlayerInvUsedSlot() == playerInvIndex;
    }

    @Unique
    private int handSlot(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? this.player.getInventory().selected : 40;
    }

    @Unique
    private void cancelAndResync(CallbackInfo ci) {
        ci.cancel();
        this.player.containerMenu.sendAllDataToRemote();
    }

    @Inject(method = "handlePlayerAction", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        switch (packet.getAction()) {
            case DROP_ITEM:
            case DROP_ALL_ITEMS:
                if (isOpenedSlot(this.player.getInventory().selected)) cancelAndResync(ci);
                break;
            case SWAP_ITEM_WITH_OFFHAND:
                if (isOpenedSlot(this.player.getInventory().selected) || isOpenedSlot(40)) cancelAndResync(ci);
                break;
            default:
                break;
        }
    }

    @Inject(method = "handleUseItemOn", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handleUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
        if (isOpenedSlot(handSlot(packet.getHand()))) {
            cancelAndResync(ci);
            BlockPos pos = packet.getHitResult().getBlockPos();
            ServerLevel level = this.player.getLevel();
            this.player.connection.send(new ClientboundBlockUpdatePacket(level, pos));
            this.player.connection.send(new ClientboundBlockUpdatePacket(level, pos.relative(packet.getHitResult().getDirection())));
        }
    }

    @Inject(method = "handleUseItem", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handleUseItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
        if (isOpenedSlot(handSlot(packet.getHand()))) ci.cancel();
    }

    @Inject(method = "handleInteract", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        InteractionHand[] hand = new InteractionHand[1];
        packet.dispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onInteraction(InteractionHand interactionHand) {
                hand[0] = interactionHand;
            }

            @Override
            public void onInteraction(InteractionHand interactionHand, Vec3 location) {
                hand[0] = interactionHand;
            }

            @Override
            public void onAttack() {
            }
        });
        if (hand[0] != null && isOpenedSlot(handSlot(hand[0]))) cancelAndResync(ci);
    }

    @Inject(method = "handlePickItem", at = @At(value = "INVOKE", target = ON_SERVER_THREAD, shift = At.Shift.AFTER), cancellable = true)
    private void QS$handlePickItem(ServerboundPickItemPacket packet, CallbackInfo ci) {
        if (isOpenedSlot(this.player.getInventory().selected) || isOpenedSlot(packet.getSlot())) cancelAndResync(ci);
    }
}
