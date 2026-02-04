package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    public void useItem(PlayerEntity player, World world, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResultHolder<ItemStack> result = QuickShulkerMod.interactItem(player, world, hand);
        if (result.getResult() != InteractionResult.PASS) {
            cir.setReturnValue(result.getResult());
            cir.cancel();
        }
    }
}
