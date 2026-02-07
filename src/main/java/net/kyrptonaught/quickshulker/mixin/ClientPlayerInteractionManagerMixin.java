package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerUseItemC2SPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.kyrptonaught.quickshulker.QuickShulkerMod.PLAYER_INVENTORY_OFF_HAND_SLOT;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    public void useItem(PlayerEntity player, World world, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getHandStack(hand);
        if (ClientUtil.CheckAndSend(stack, hand == InteractionHand.MAIN_HAND ? player.inventory.selectedSlot : PLAYER_INVENTORY_OFF_HAND_SLOT)) {
            cir.cancel();
        }
        //InteractionResultHolder<ItemStack> resultHolder = QuickShulkerMod.interactItem(player, world, hand);
        //InteractionResult result = resultHolder.getResult();
        //if (result != InteractionResult.PASS) {
        //    if (result == InteractionResult.SUCCESS) {
        //        this.networkHandler.sendPacket(new PlayerUseItemC2SPacket(hand));

        //    }
        //    cir.setReturnValue(result);
        //    cir.cancel();
        //}
    }
}
