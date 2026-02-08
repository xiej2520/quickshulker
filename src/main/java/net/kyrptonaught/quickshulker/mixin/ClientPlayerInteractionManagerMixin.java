package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.config.ClientConfigs;
import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.kyrptonaught.quickshulker.client.QuickShulkerModClient.PLAYER_INVENTORY_OFF_HAND_SLOT;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    public void useItem(PlayerEntity player, World world, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (ClientConfigs.Options.RIGHT_CLICK_OPEN_IN_HAND.getValue()) {
            if (player.getMainHandStack().isEmpty() && !player.getOffHandStack().isEmpty()) {
                if (ClientUtil.tryOpenAndSendPacket(player.getOffHandStack(), PLAYER_INVENTORY_OFF_HAND_SLOT)) {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            } else {
                if (ClientUtil.tryOpenAndSendPacket(player.getMainHandStack(), player.inventory.selectedSlot)) {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
